terraform {
  required_providers {
    alicloud = {
      source = "aliyun/alicloud"
      version = "1.140.0"
    }
  }
}

resource "alicloud_ecs_disk" "disk" {
  zone_id   = var.aliyun_instance.data_disk_zone_id
  disk_name = var.aliyun_instance.data_disk_name
  category  = var.aliyun_instance.data_disk_category
  size      = var.aliyun_instance.data_disk_size
}

resource "alicloud_instance" "instance" {
  availability_zone          = var.aliyun_instance.instance_availability_zone
  security_groups            = var.aliyun_instance.security_groups
  instance_type              = var.aliyun_instance.instance_type
  system_disk_category       = var.aliyun_instance.system_disk_category
  image_id                   = var.aliyun_instance.image_id
  instance_name              = var.aliyun_instance.instance_name
  host_name                  = var.aliyun_instance.host_name
  vswitch_id                 = var.aliyun_instance.vswitch_id
  internet_max_bandwidth_out = var.aliyun_instance.internet_max_bandwidth_out
  tags                       = var.aliyun_instance.tags
  user_data                  = <<-EOT
    #!/bin/sh
    set -xe
    bash /root/setup-disk.sh /dev/vdb /data
  EOT
}

resource "alicloud_ecs_disk_attachment" "disk_attachment" {
  disk_id     = alicloud_ecs_disk.disk.id
  instance_id = alicloud_instance.instance.id
}