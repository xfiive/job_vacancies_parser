package org.parser.parsermail.bot.services;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.parser.parsermail.entities.JobVacancy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
public class MessageService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Value("${bot.my-chat-id}")
    private String myChatId;

    public void scheduleAndSendMessages(@NotNull List<JobVacancy> jobs, int intervalInSeconds, TelegramLongPollingBot bot) {
        IntStream.range(0, jobs.size())
                .forEach(index -> {
                    JobVacancy job = jobs.get(index);
                    scheduler.schedule(() -> {
                        SendMessage message = new SendMessage();
                        message.setChatId(myChatId);
                        message.setText(this.createJobVacancyMessageText(job));
                        try {
                            bot.execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }, (long) index * intervalInSeconds, TimeUnit.SECONDS);
                });


    }

    private @NotNull String createJobVacancyMessageText(@NotNull JobVacancy jobVacancy) {
        StringBuilder sb = new StringBuilder();

        sb.append(this.formatJobNameLine(jobVacancy.getJobName()));
        sb.append("\n");
        sb.append(this.formatCompanyNameLine(jobVacancy.getCompanyName()));
        sb.append("\n\n");
        sb.append(this.formatJobLocationLine(jobVacancy.getJobLocation()));
        sb.append("\n");
        sb.append(this.formatSalaryLine(jobVacancy.getSalary()));
        sb.append("\n");
        sb.append(this.formatContractTypeLine(jobVacancy.getContractType()));
        sb.append("\n\n");
        sb.append(this.formatJobLinkLine(jobVacancy.getJobLink()));

        return sb.toString();
    }

    @Contract(pure = true)
    private @NotNull String formatJobLinkLine(String link) {
        return "Job link: " + link + "\n";
    }

    @Contract(pure = true)
    private @NotNull String formatContractTypeLine(@NotNull String contractType) {
        return "Contract type: " + contractType + "\n";
    }

    @Contract(pure = true)
    private @NotNull String formatSalaryLine(int salary) {
        if (10000 < salary) {
            String salaryStr = Integer.toString(salary);

            if (salaryStr.length() != 8) {
                return "Salary: " + salary + "\n";
            }

            String firstPart = salaryStr.substring(0, 4);
            String secondPart = salaryStr.substring(4, 8);

            return "Salary: " + firstPart + "-" + secondPart + " EUR/month\n";
        }
        if (100 < salary) {
            return "Salary: " + salary + "  EUR/month\n";
        } else {
            if (salary < 100) {
                return "Salary: " + salary + " EUR/hour\n";
            } else
                return "Salary: N/A\n";
        }
    }

    @Contract(pure = true)
    private @NotNull String formatJobLocationLine(@NotNull String jobLocation) {
        return "Job location: " + jobLocation + "\n";
    }

    @Contract(pure = true)
    private @NotNull String formatCompanyNameLine(@NotNull String companyName) {
        return "Company name: " + companyName + "\n";
    }

    @Contract(pure = true)
    private @NotNull String formatJobNameLine(@NotNull String jobName) {
        return "Vacancy name: " + jobName + "\n";
    }

}
