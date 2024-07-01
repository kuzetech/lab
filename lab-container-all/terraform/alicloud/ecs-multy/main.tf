provider "alicloud" {}

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
  port_range        = "1/65535"
  priority          = 1
  security_group_id = alicloud_security_group.default.id
  cidr_ip           = "0.0.0.0/0"
}

module "tf-instances" {  
  source                      = "alibaba/ecs-instance/alicloud"  
  region                      = "cn-hangzhou"  
  number_of_instances         = "2"  
  vswitch_id                  = alicloud_vswitch.vsw.id  
  group_ids                   = [alicloud_security_group.default.id]  
  private_ips                 = ["172.16.0.10", "172.16.0.11"]  
  image_ids                   = ["centos_7_06_64_20G_alibase_20190711.vhd"]  
  instance_type               = "ecs.xn4.small"   
  internet_max_bandwidth_out  = 1
  associate_public_ip_address = true  
  instance_name               = "my_module_instances_"  
  host_name                   = "sample"  
  internet_charge_type        = "PayByTraffic"   
  password                    = "User@123" 
  system_disk_category        = "cloud_efficiency"  
}