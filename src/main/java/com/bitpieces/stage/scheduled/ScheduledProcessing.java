package com.bitpieces.stage.scheduled;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduledProcessing {
	public static void main(String[] args) {
		// Another
        try {
            // Grab the Scheduler instance from the Factory 
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            
            // and start it off
            scheduler.start();

            // testing a job
            // define the job and tie it to our HelloJob class
            JobDetail job = newJob(AskBidAccepter.class)
//                .withIdentity("job1", "group1")
                .build();
            
            JobDetail job2 = newJob(UpdateTransactionStatuses.class)
//                  .withIdentity("job1", "group1")
                  .build();
            
            JobDetail job3 = newJob(CreatorsFundsChecker.class)
//                  .withIdentity("job1", "group1")
                  .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
//                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(60)
                        .repeatForever())
                .build();
            
            Trigger trigger2 = newTrigger()
//                  .withIdentity("trigger1", "group1")
                  .startNow()
                  .withSchedule(simpleSchedule()
                          .withIntervalInSeconds(60)
                          .repeatForever())            
                  .build();
            
            Trigger trigger3 = newTrigger()
//                  .withIdentity("trigger1", "group1")
                  .startNow()
                  .withSchedule(simpleSchedule()
                          .withIntervalInSeconds(60)
                          .repeatForever())            
                  .build();
            
            

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);
            scheduler.scheduleJob(job2, trigger2);
            scheduler.scheduleJob(job3, trigger3);
//            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
}
