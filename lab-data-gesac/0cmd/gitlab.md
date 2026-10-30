## 修改配置文件
vi /etc/gitlab/gitlab.rb
gitlab-ctl reconfigure

## 启停命令
gitlab-ctl restart
gitlab-ctl start
gitlab-ctl stop

## 查看 nginx 连接日志
gitlab-ctl tail nginx

## 测试邮件发送
gitlab-rails console
Notify.test_email('huang.shaowei2@cxtc.com', 'GitLab SMTP 测试', '这是一封测试邮件').deliver_now

## 邮件凭证加密
gitlab-rake gitlab:smtp:secret:show
gitlab-rake gitlab:smtp:secret:edit EDITOR=vim