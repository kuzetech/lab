# Input variable definitions

variable "aliyun_instance" {
  type = object({
    instance_availability_zone = string
    image_id                   = string
    security_groups            = list(string)
    vswitch_id                 = string
    internet_max_bandwidth_out = number
    instance_type              = string
    instance_name              = string
    host_name                  = string
    system_disk_category       = string
    data_disk_zone_id          = string
    data_disk_name             = string
    data_disk_category         = string
    data_disk_size             = number
    tags                       = map(string)
  })
}
