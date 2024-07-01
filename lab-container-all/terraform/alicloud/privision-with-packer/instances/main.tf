provider "alicloud" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region     = "cn-hangzhou"
}

resource "alicloud_vpc" "vpc" {
  vpc_name       = "tf_test_foo"
  cidr_block = "172.16.0.0/12"
}

resource "alicloud_vswitch" "vsw" {
  vpc_id            = alicloud_vpc.vpc.id
  cidr_block        = "172.16.0.0/21"
  zone_id = "cn-hangzhou-b"
}

resource "alicloud_security_group" "default" {
  name = "default"
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
  availability_zone = "cn-hangzhou-b"
  security_groups = alicloud_security_group.default.*.id
  instance_type        = "ecs.xn4.small"
  system_disk_category = "cloud_efficiency"
  image_id             = "${var.image_id}"
  instance_name        = "test_foo"
  vswitch_id = alicloud_vswitch.vsw.id
  internet_max_bandwidth_out = 1
}