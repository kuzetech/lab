resource "alicloud_ess_scaling_group" "scaling" {
  min_size = 2
  max_size = 4
  scaling_group_name = "tf-scaling"
  vswitch_ids=["${alicloud_vswitch.vsw.id}"]
  loadbalancer_ids = ["${alicloud_slb_load_balancer.slb.id}"]
  removal_policies   = ["OldestInstance", "NewestInstance"]
  depends_on = ["alicloud_slb_listener.http"]
}

resource "alicloud_ess_scaling_configuration" "config" {
  scaling_group_id = "${alicloud_ess_scaling_group.scaling.id}"
  image_id = "centos_7_06_64_20G_alibase_20190711.vhd"
  instance_type = "ecs.xn4.small"
  security_group_id = "${alicloud_security_group.default.id}"
  active=true
  enable=true
  user_data = "#!/bin/bash\necho \"Hello, World\" > index.html\nnohup busybox httpd -f -p 8080&"
  internet_max_bandwidth_in=10
  internet_max_bandwidth_out=10
  internet_charge_type = "PayByTraffic"
  force_delete= true
}

resource "alicloud_ess_scaling_rule" "rule" {
  scaling_group_id = "${alicloud_ess_scaling_group.scaling.id}"
  adjustment_type  = "TotalCapacity"
  adjustment_value = 2
  cooldown = 60
}