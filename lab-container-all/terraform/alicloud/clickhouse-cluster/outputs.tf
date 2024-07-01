
output "master_ips" {
  value = {
    for k, v in module.master_instance : k => v.instance.public_ip
  }
}

output "worke_ips" {
  value = {
    for k, v in module.worker_instance : k => v.instance.public_ip
  }
}