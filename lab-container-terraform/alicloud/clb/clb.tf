resource "alicloud_slb_load_balancer" "slb" {
  load_balancer_name       = "test-slb-tf"
  vswitch_id = "${alicloud_vswitch.vsw.id}"
  address_type = "internet"
  load_balancer_spec = "slb.s1.small"
  master_zone_id = "cn-hangzhou-b"
  bandwidth = 1
  internet_charge_type = "PayByTraffic"
}

resource "alicloud_slb_listener" "http" {
  load_balancer_id = "${alicloud_slb_load_balancer.slb.id}"
  backend_port = 8080
  frontend_port = 80
  bandwidth = 10
  protocol = "http"
  sticky_session = "on"
  sticky_session_type = "insert"
  cookie = "testslblistenercookie"
  cookie_timeout = 86400
  health_check="on"
  health_check_type = "http"
  health_check_connect_port = 8080
}