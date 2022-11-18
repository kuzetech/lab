FROM golang:1.18-bullseye as builder

ENV GOPROXY=https://goproxy.cn,direct

RUN mkdir -p /app/simulator

WORKDIR /app/simulator

COPY ./* ./

RUN go mod download -x && go build .

CMD ["./lab-go-log-simulator"]
