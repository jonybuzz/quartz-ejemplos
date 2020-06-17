package com.example.demoquartz;

import java.util.concurrent.CountDownLatch;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class QuartzSchedulerExample implements ILatch {

    private static int REPEAT_TIMES = 3;
    private CountDownLatch contador = new CountDownLatch(REPEAT_TIMES + 1);

    public static void main(String[] args) throws Exception {
        QuartzSchedulerExample quartzSchedulerExample = new QuartzSchedulerExample();
        quartzSchedulerExample.fireJob();
    }

    
    public void fireJob() throws SchedulerException, InterruptedException {
        
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
        Scheduler scheduler = schedFact.getScheduler();
        scheduler.start();


        JobBuilder jobBuilder = JobBuilder.newJob(JobImpl.class);
        JobDataMap data = new JobDataMap();
        data.put("latch", this);


        JobDetail jobDetail = jobBuilder.usingJobData("example", "QuartzSchedulerExample")
                .usingJobData(data)
                .withIdentity("myJob", "group1")
                .build();


        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .withRepeatCount(REPEAT_TIMES)
                        .withIntervalInSeconds(2))
                .build();


        scheduler.scheduleJob(jobDetail, trigger);
        contador.await();
        System.out.println("All triggers executed. Shutdown scheduler");
        scheduler.shutdown();
    }

    public void countDown() {
        contador.countDown();
    }
}
