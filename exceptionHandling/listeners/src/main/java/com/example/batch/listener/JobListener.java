package com.example.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class JobListener implements JobExecutionListener {

	private JavaMailSender mailSender;

	public JobListener(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName();

/*		SimpleMailMessage mail =
				getSimpleMailMessage(String.format("%s is starting", jobName),
						String.format("Per your request, we are informing you that %s is starting",
				jobName));

		mailSender.send(mail);*/
		System.out.println("Mailing before the job starts:"+jobName);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
/*		String jobName = jobExecution.getJobInstance().getJobName();

		SimpleMailMessage mail =
				getSimpleMailMessage(String.format("%s has completed", jobName),
						String.format("Per your request, we are informing you that %s has completed",
								jobName));

		mailSender.send(mail);*/
		System.out.println("Mailing after the job completes:"+jobExecution.getJobInstance().getJobName());

	}

	private SimpleMailMessage getSimpleMailMessage(String subject, String text) {
		SimpleMailMessage mail = new SimpleMailMessage();

		mail.setTo("kalyandas75@gmail.com");
		mail.setSubject(subject);
		mail.setText(text);
		return mail;
	}
}
