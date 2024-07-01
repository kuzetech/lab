locals {
  master_instances = {
    master-1 = {
      instance_availability_zone = "cn-hangzhou-b"
      image_id                   = "m-bp17xw1100pknxkonj9y"
      security_groups            = alicloud_security_group.default.*.id
      vswitch_id                 = alicloud_vswitch.vsw.id
      instance_type              = "ecs.xn4.small"
      instance_name              = "master-1"
      host_name                  = "master-1"
      system_disk_category       = "cloud_efficiency"
      data_disk_zone_id          = "cn-hangzhou-b"
      data_disk_name             = "master-disk-1"
      data_disk_category         = "cloud_efficiency"
      data_disk_size             = 20
      internet_max_bandwidth_out = 1
      tags = {
        app  = "clickhouse"
        role = "master"
      }
    }
    master-2 = {
      instance_availability_zone = "cn-hangzhou-b"
      image_id                   = "m-bp17xw1100pknxkonj9y"
      security_groups            = alicloud_security_group.default.*.id
      vswitch_id                 = alicloud_vswitch.vsw.id
      instance_type              = "ecs.xn4.small"
      instance_name              = "master-2"
      host_name                  = "master-2"
      system_disk_category       = "cloud_efficiency"
      data_disk_zone_id          = "cn-hangzhou-b"
      data_disk_name             = "master-disk-2"
      data_disk_category         = "cloud_efficiency"
      data_disk_size             = 20
      internet_max_bandwidth_out = 1
      tags = {
        app  = "clickhouse"
        role = "master"
      }
    }
    master-3 = {
      instance_availability_zone = "cn-hangzhou-b"
      image_id                   = "m-bp17xw1100pknxkonj9y"
      security_groups            = alicloud_security_group.default.*.id
      vswitch_id                 = alicloud_vswitch.vsw.id
      instance_type              = "ecs.xn4.small"
      instance_name              = "master-3"
      host_name                  = "master-3"
      system_disk_category       = "cloud_efficiency"
      data_disk_zone_id          = "cn-hangzhou-b"
      data_disk_name             = "master-disk-3"
      data_disk_category         = "cloud_efficiency"
      data_disk_size             = 20
      internet_max_bandwidth_out = 1
      tags = {
        app  = "clickhouse"
        role = "master"
      }
    }
  }

  worker_instances = {
    worker-1 = {
      instance_availability_zone = "cn-hangzhou-b"
      image_id                   = "m-bp17xw1100pknxkonj9y"
      security_groups            = alicloud_security_group.default.*.id
      vswitch_id                 = alicloud_vswitch.vsw.id
      instance_type              = "ecs.xn4.small"
      instance_name              = "worker-1"
      host_name                  = "worker-1"
      system_disk_category       = "cloud_efficiency"
      data_disk_zone_id          = "cn-hangzhou-b"
      data_disk_name             = "worker-disk-1"
      data_disk_category         = "cloud_efficiency"
      data_disk_size             = 20
      internet_max_bandwidth_out = 1
      tags = {
        app  = "clickhouse"
        role = "worker"
      }
    }
  }
}