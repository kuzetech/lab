provider "alicloud" {}

resource "alicloud_vpc" "vpc" {
  vpc_name       = "tf_test_foo"
  cidr_block = "172.16.0.0/12"
}

data "alicloud_zones" "zones_ds" {
  available_instance_type = "ecs.xn4.small"
}

resource "alicloud_vswitch" "vsw" {
  vpc_id            = alicloud_vpc.vpc.id
  cidr_block        = "172.16.0.0/21"
  zone_id = var.zone_id != "" ? var.zone_id : data.alicloud_zones.zones_ds.ids[0]
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

resource "alicloud_resource_manager_resource_group" "mrg" {
  resource_group_name = "testrd"
  display_name        = "testrd"
}