variable "access_key" {
  type = string
  default = "xxxxxxx"
}

variable "secret_key" {
  type = string
  default = "xxxxxxx"
}

variable "region" {
  type = string
  default = "cn-hangzhou"
}

source "alicloud-ecs" "basic-example" {
      access_key = var.access_key
      secret_key = var.secret_key
      region = var.region
      image_name = "packer_test"
      source_image = "centos_7_06_64_20G_alibase_20190711.vhd"
      image_ignore_data_disks = true
      ssh_username = "root"
      instance_type = "ecs.xn4.small"
      io_optimized = true
      internet_charge_type = "PayByTraffic"
      image_force_delete = true
}

build {
  sources = ["sources.alicloud-ecs.basic-example"]

  provisioner "shell" {
    inline = [
      "echo 1 > /tmp/test1.txt", 
      "echo 2 > /tmp/test2.txt"
    ]
  }

  provisioner "file" {
    source      = "../ssh/id_rsa.pub"
    destination = "/root/.ssh/authorized_keys"
  }

  provisioner "shell" {
    script = "../scripts/setup.sh"
  }

}

