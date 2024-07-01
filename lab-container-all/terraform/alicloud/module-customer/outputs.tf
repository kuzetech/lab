output "website_bucket_domain" {
  description = "Domain name of the bucket"
  value       = module.website_oss_bucket.domain
}