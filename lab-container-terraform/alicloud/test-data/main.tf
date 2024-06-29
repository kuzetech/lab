
provider "alicloud" {}

data "alicloud_slb_zones" "zones_ids" {
    enable_details = true
}

data "alicloud_images" "images" {
  owners     = "system"
  name_regex = "^ubuntu_"
  architecture = "x86_64"
}

output "slb_zones"{
  value = "${data.alicloud_slb_zones.zones_ids}"
}

output "ubuntu_iamges"{
  value = "${data.alicloud_images.images.ids}"
}