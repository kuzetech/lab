terraform {
  required_providers {
    alicloud = {
      source  = "aliyun/alicloud"
      version = "1.140.0"
    }
  }
}

resource "alicloud_vpc" "vpc" {
  vpc_name   = "tf_test"
  cidr_block = "172.16.0.0/12"
}

resource "alicloud_vswitch" "vsw" {
  vpc_id     = alicloud_vpc.vpc.id
  cidr_block = "172.16.0.0/21"
  zone_id    = "cn-hangzhou-b"
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
  port_range        = "1/65533"
  priority          = 1
  security_group_id = alicloud_security_group.default.id
  cidr_ip           = "0.0.0.0/0"
}

module "master_instance" {
  source          = "./modules/node"
  for_each        = local.master_instances
  aliyun_instance = each.value
}

module "worker_instance" {
  source          = "./modules/node"
  for_each        = local.worker_instances
  aliyun_instance = each.value
}

