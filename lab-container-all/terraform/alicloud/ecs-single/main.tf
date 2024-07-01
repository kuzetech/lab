terraform {
  required_providers {
    alicloud = {
      source  = "aliyun/alicloud"
      version = "1.140.0"
    }
  }
}

resource "alicloud_vpc" "vpc" {
  vpc_name   = "tf_test_foo"
  cidr_block = "172.16.0.0/12"
}

resource "alicloud_vswitch" "vsw" {
  vpc_id     = alicloud_vpc.vpc.id
  cidr_block = "172.16.0.0/21"
  zone_id    = "cn-hangzhou-i"
}

resource "alicloud_security_group" "default" {
  name   = "default"
  vpc_id = alicloud_vpc.vpc.id
}

resource "alicloud_security_group_rule" "allow_all_tcp" {
  type              = "ingress"
  ip_protocol       = "tcp"
  nic_type          = "intranet"
  policy            = "accept"
  port_range        = "22/22"
  priority          = 1
  security_group_id = alicloud_security_group.default.id
  cidr_ip           = "0.0.0.0/0"
}

resource "alicloud_instance" "instance" {
  availability_zone          = alicloud_vswitch.vsw.zone_id
  security_groups            = alicloud_security_group.default.*.id
  instance_type              = "ecs.xn4.small"
  instance_charge_type       = "PostPaid"
  period_unit                = "Month"
  period                     = 1
  system_disk_category       = "cloud_efficiency"
  image_id                   = "m-bp1h3nconzn3eno6vc48"
  instance_name              = "test-foo"
  host_name                  = "test-foo"
  vswitch_id                 = alicloud_vswitch.vsw.id
  private_ip                 = "172.16.0.16"
  internet_max_bandwidth_out = 3
  internet_charge_type       = "PayByTraffic"
  key_name                   = "hsw"
  user_data                  = <<-EOF
    #!/bin/bash
    set -xeu
    # 修改 cloudinit 配置，让 ECS 每次启动都运行这段脚本
    sed -i 's/scripts-user$/\[scripts-user, always\]/' /etc/cloud/cloud.cfg
    bash /root/setup-disk.sh /dev/vdb /data
    sudo service clickhouse-server restart
  EOF
}

resource "alicloud_ecs_disk" "data" {
  zone_id      = alicloud_vswitch.vsw.zone_id
  disk_name    = "my-disk"
  category     = "cloud_efficiency"
  size         = 60
  type         = "online"
}

resource "alicloud_ecs_disk_attachment" "data" {
  disk_id     = alicloud_ecs_disk.data.id
  instance_id = alicloud_instance.instance.id
}



