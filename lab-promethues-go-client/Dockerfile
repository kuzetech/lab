FROM golang:1.18-bullseye as builder

ENV GOPROXY=https://goproxy.cn,direct

RUN mkdir -p /app

WORKDIR /app

COPY . .

RUN go mod download -x && go build .

CMD ["./lab-promethues-go-client"]
