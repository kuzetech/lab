package main

import (
	"github.com/go-co-op/gocron/v2"
	"github.com/google/uuid"
	"time"
)

func main() {
	s, err := gocron.NewScheduler(
		gocron.WithGlobalJobOptions(
			gocron.WithTags("tag1", "tag2", "tag3"),
			gocron.WithEventListeners(
				gocron.BeforeJobRuns(func(jobID uuid.UUID, jobName string) {
					Logger.Info().Str("jobID", jobID.String()).Str("jobName", jobName).Msg("开始执行")
				}),
				gocron.AfterJobRuns(func(jobID uuid.UUID, jobName string) {
					Logger.Info().Str("jobID", jobID.String()).Str("jobName", jobName).Msg("结束执行")
				}),
				gocron.AfterJobRunsWithError(func(jobID uuid.UUID, jobName string, err error) {
					Logger.Error().Err(err).Str("jobID", jobID.String()).Str("jobName", jobName).Msg("运行遇到错误")
				}),
				gocron.AfterJobRunsWithPanic(func(jobID uuid.UUID, jobName string, recoverData any) {
					// AfterJobRunsWithPanic is used to listen for when a job has run and returned panicked recover data, and then run the provided
					Logger.Error().Any("recoverData", recoverData).Str("jobID", jobID.String()).Str("jobName", jobName).Msg("运行遇到不可恢复的错误")
				}),
			),
		),
		gocron.WithLogger(gocron.NewLogger(gocron.LogLevelInfo)),
		gocron.WithLimitConcurrentJobs(2, gocron.LimitModeWait),
	)
	if err != nil {
		panic(err)
	}

	_, err = s.NewJob(
		gocron.CronJob("30 * * * * *", true),
		gocron.NewTask(func() error {
			for i := 0; i < 3; i++ {
				_, err2 := s.NewJob(
					gocron.OneTimeJob(gocron.OneTimeJobStartImmediately()),
					gocron.NewTask(func() {
						time.Sleep(time.Second * 5)
					}),
					gocron.WithName(getJobName("second")),
				)
				if err2 != nil {
					return err2
				}
			}
			return nil
		}),
		gocron.WithName("first"),
	)
	if err != nil {
		panic(err)
	}

	s.Start()

	waitingForAppTerm()

	err = s.Shutdown()
	if err != nil {
		panic(err)
	}
}
