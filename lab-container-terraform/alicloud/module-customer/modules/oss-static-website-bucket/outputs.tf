# Output variable definitions

output "domain" {
  description = "Domain name of the bucket"
  value       = alicloud_oss_bucket.bucket-website
}
