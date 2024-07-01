# Terraform configuration

resource "alicloud_oss_bucket" "bucket-website" {
  bucket = var.bucket_name
  acl    = "public-read"

  policy = <<POLICY
  {"Statement":
      [{"Action":
          ["oss:PutObject", "oss:GetObject", "oss:DeleteBucket"],
        "Effect":"Allow",
        "Resource":
            ["acs:oss:*:*:*"]}],
   "Version":"1"}
  POLICY

  website {
    index_document = "index.html"
    error_document = "error.html"
  }

  tags = var.tags
}
