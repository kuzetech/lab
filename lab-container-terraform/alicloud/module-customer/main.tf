provider "alicloud" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region     = "cn-hangzhou"
}

module "website_oss_bucket" {
  source = "./modules/oss-static-website-bucket"

  bucket_name = "robin-test-dec-17-2019"

  tags = {
    Terraform   = "true"
    Environment = "dev"
  }
}