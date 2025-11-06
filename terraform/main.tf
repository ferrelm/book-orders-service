provider "aws" {
  region = var.aws_region
}

resource "aws_ecr_repository" "book_orders_repo" {
  name = "book-orders-service"
}
