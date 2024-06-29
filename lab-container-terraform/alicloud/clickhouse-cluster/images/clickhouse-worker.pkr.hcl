variable "instance_type" {
  type    = string
  default = "ecs.xn4.small"
}

variable "image_name" {
  type    = string
  default = "ubuntu_20_04_x64_20G_clickhouse_worker_20211027"
}

source "alicloud-ecs" "ubuntu-clickhouse-worker" {
  image_name              = var.image_name
  source_image            = "ubuntu_20_04_x64_20G_alibase_20210927.vhd"
  image_ignore_data_disks = true
  ssh_username            = "root"
  instance_type           = var.instance_type
  io_optimized            = true
  internet_charge_type    = "PayByTraffic"
  image_force_delete      = true
}

build {
  sources = ["sources.alicloud-ecs.ubuntu-clickhouse-worker"]

  provisioner "file" {
    source      = "~/.ssh/id_rsa.pub"
    destination = "/root/.ssh/authorized_keys"
  }

  provisioner "file" {
    source      = "./scripts/setup-disk.sh"
    destination = "/root/setup-disk.sh"
  }

  provisioner "shell" {
    script = "./scripts/worker-setup.sh"
  }

}

