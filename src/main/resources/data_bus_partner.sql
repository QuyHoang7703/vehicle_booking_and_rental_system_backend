-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: vehicle_booking_rental
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
(2,_binary '',NULL,NULL,'hongnhung16052003@gmail.com',NULL,NULL,NULL,NULL,NULL,'$2a$10$4p00vCG3EjvYSi3PA1com.saV9hBL3T4Ew7JkONGPH8B3QTDMHmP.',NULL,'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJob25nbmh1bmcxNjA1MjAwM0BnbWFpbC5jb20iLCJleHAiOjE3MzIzODU4OTMsImlhdCI6MTczMjMxMzg5MywidXNlciI6eyJpZCI6MiwiZW1haWwiOiJob25nbmh1bmcxNjA1MjAwM0BnbWFpbC5jb20iLCJuYW1lIjpudWxsLCJwaG9uZU51bWJlciI6bnVsbCwiZ2VuZGVyIjpudWxsLCJhdmF0YXIiOm51bGwsImFjdGl2ZSI6dHJ1ZSwicm9sZXMiOlsiVVNFUiIsIkJVU19QQVJUTkVSIl19fQ.JnZ5z-bZNQci30uA7kAxcudDPMUZ9nlGCPiZsD4kD7tsxAg3ms08udrpnQGnDmiuXhgusYRRXKo48BK8zF5RHw',NULL,_binary '');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `account_role`
--

LOCK TABLES `account_role` WRITE;
/*!40000 ALTER TABLE `account_role` DISABLE KEYS */;
INSERT INTO `account_role` VALUES (1,_binary '',NULL,'2024-11-22 03:01:26.327050',NULL,1,1),(2,_binary '',NULL,'2024-11-22 07:29:25.044843',NULL,2,2),(3,_binary '',NULL,'2024-11-22 07:32:33.085874',NULL,2,3);
/*!40000 ALTER TABLE `account_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `bank_account`
--

LOCK TABLES `bank_account` WRITE;
/*!40000 ALTER TABLE `bank_account` DISABLE KEYS */;
INSERT INTO `bank_account` VALUES (1,'Nguyễn Hữu Thọ','8ulPt9YnMqG1/ijSuyunxw==','LoPm4gg/jwl3Slrk7bGg4kxwDvslYP7d24cJSNUPbFdfeVCUOabnhZ3Dnygv1oYoR2w3010BKQW7RuSmBIf2yo9gOA3c48+Uxh3RM66sPQR7bMVCwysUtNaZMKXMcJGZ7yqmxC5hN3MqXk4srHVJOt4AI76sDtzvI7DGYZvYZIOX4xIvdFi2w2c7W/zaqlTLoYYmf2U+ZaDEYRd2/kGJZQQJGfSwALU9ewmytLusNEuLEeCskcenITbS3WfE7yl338kqTqUGBFjjPNjnS9vjNNADHTyYvv2bqjRCUxQ2atn7PaX1SNKUovAZ3xEtWytey1iVhR9em4RmM+D4/NxRlg==','Ngân hàng ABC','BUS_PARTNER',2);
/*!40000 ALTER TABLE `bank_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `break_day`
--

LOCK TABLES `break_day` WRITE;
/*!40000 ALTER TABLE `break_day` DISABLE KEYS */;
INSERT INTO `break_day` VALUES (1,'2024-12-25','2024-11-25',2),(2,'2024-01-03','2024-01-01',2),(3,'2024-12-25','2024-12-22',4),(4,'2024-01-03','2024-01-01',4),(5,'2024-12-25','2024-12-09',5),(7,'2024-12-25','2024-12-22',6),(8,'2024-01-03','2024-01-01',6),(9,'2024-12-25','2024-12-22',7),(10,'2024-01-03','2024-01-01',7),(11,'2024-12-25','2024-12-22',8),(12,'2024-01-03','2024-01-01',8),(13,'2024-12-25','2024-12-22',9),(14,'2024-01-03','2024-01-01',9),(17,'2024-12-25','2024-12-24',11),(18,'2024-01-03','2024-01-01',11),(19,'2024-12-25','2024-12-15',12),(20,'2024-01-03','2024-01-01',12);
/*!40000 ALTER TABLE `break_day` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `bus_partner`
--

LOCK TABLES `bus_partner` WRITE;
/*!40000 ALTER TABLE `bus_partner` DISABLE KEYS */;
INSERT INTO `bus_partner` VALUES (1,'Nhà xe đến từ Nghệ An được thành lập vào năm 2010, chuyên phục vụ các chuyến đi xuyên tỉnh từ Đà Nẵng - Nghệ An, Nghệ An - Hà Nội, HCM','thanhhongson1995.com.vn',1);
/*!40000 ALTER TABLE `bus_partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `bus_trip`
--

LOCK TABLES `bus_trip` WRITE;
/*!40000 ALTER TABLE `bus_trip` DISABLE KEYS */;
INSERT INTO `bus_trip` VALUES (1,'Nam Định','Đà Nẵng','Bến xe trung tâm Nam Định!Quận 1 Nam Định',25200000000000,'Bến xe trung tâm Đà Nẵng!Bến xe miền Đông Đà Nẵng',1),(2,'Hà Nội','Đà Nẵng','Bến xe Mỹ Đình Hà Nội!Bến xe Gia Lâm Hà Nội',43200000000000,'Bến xe trung tâm Đà Nẵng!Bến xe Liên Chiểu Đà Nẵng',1),(3,'Vũng Tàu','TP.HCM','Bến xe Vũng Tàu!Bãi Sau Vũng Tàu',9000000000000,'Bến xe Miền Đông TP.HCM!Bến xe An Sương TP.HCM',1);
/*!40000 ALTER TABLE `bus_trip` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `bus_trip_schedule`
--

LOCK TABLES `bus_trip_schedule` WRITE;
/*!40000 ALTER TABLE `bus_trip_schedule` DISABLE KEYS */;
INSERT INTO `bus_trip_schedule` VALUES (2,9,'20:00:00.000000',0,_binary '',400000,0,'2024-11-20',1,1),(3,20,'04:00:00.000000',0,_binary '',400000,0,'2024-11-20',2,1),(4,34,'04:00:00.000000',0,_binary '',400000,0,'2024-11-23',3,1),(5,34,'22:00:00.000000',10,_binary '',400000,0,'2024-11-20',3,1),(6,34,'12:00:00.000000',0,_binary '',500000,0,'2024-11-24',4,1),(7,34,'20:00:00.000000',0,_binary '',600000,0,'2024-11-20',4,2),(8,34,'02:00:00.000000',0,_binary '',700000,0,'2024-11-24',5,1),(9,34,'23:00:00.000000',0,_binary '',700000,0,'2024-11-20',5,2),(11,20,'03:00:00.000000',10,_binary '\0',350000,0,'2024-11-24',6,1),(12,20,'03:00:00.000000',10,_binary '',350000,0,'2024-11-22',7,1);
/*!40000 ALTER TABLE `bus_trip_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `bus_types`
--

LOCK TABLES `bus_types` WRITE;
/*!40000 ALTER TABLE `bus_types` DISABLE KEYS */;
INSERT INTO `bus_types` VALUES (1,'Giường nằm đôi cao cấp','Limousine đôi giường nằm 20 chỗ',20,1),(2,'Giường nằm cao cấp','Limousine giường nằm 34 chỗ',34,1),(3,'Ghế ngồi cao cấp','Xe ghế ngồi Limousine 9 chỗ',9,1);
/*!40000 ALTER TABLE `bus_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `bus_utilities`
--

LOCK TABLES `bus_utilities` WRITE;
/*!40000 ALTER TABLE `bus_utilities` DISABLE KEYS */;
INSERT INTO `bus_utilities` VALUES (1,1),(1,2),(2,1),(2,2),(3,1),(3,2),(4,1),(4,2),(5,1),(5,2),(6,1),(6,2),(7,1),(7,2);
/*!40000 ALTER TABLE `bus_utilities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `buses`
--

LOCK TABLES `buses` WRITE;
/*!40000 ALTER TABLE `buses` DISABLE KEYS */;
INSERT INTO `buses` VALUES (1,'2024-11-22 07:40:24.166004','29B-456633',NULL,1,3),(2,'2024-11-22 07:40:46.099928','29B-456622',NULL,1,1),(3,'2024-11-22 07:40:56.879332','29B-456611',NULL,1,2),(4,'2024-11-22 07:52:16.464203','29B-456612',NULL,1,2),(5,'2024-11-22 07:57:39.133559','29B-456623',NULL,1,2),(6,'2024-11-22 10:01:51.307394','33G-191171',NULL,1,1),(7,'2024-11-22 10:06:35.233729','34G-191171',NULL,1,1);
/*!40000 ALTER TABLE `buses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `business_partner`
--

LOCK TABLES `business_partner` WRITE;
/*!40000 ALTER TABLE `business_partner` DISABLE KEYS */;
INSERT INTO `business_partner` VALUES (1,'16 Thái Hòa, Nghệ An',0,'https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/8bbb1b18-2903-4588-b9cd-a77ddec44fc5_nhaxeTuLac.jpg','Nhà xe Tú Lạc 1','thanhhongson1995@gmail.com','Nguyễn Thị Thảo',0,'0912348192','Chính sách hoàn tiền trong 24 giờ nếu chuyến xe bị hủy!Đảm bảo an toàn',2);
/*!40000 ALTER TABLE `business_partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `images`
--

LOCK TABLES `images` WRITE;
/*!40000 ALTER TABLE `images` DISABLE KEYS */;
INSERT INTO `images` VALUES (1,'BUS_PARTNER',1,'BUSINESS_LICENSE','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/b9ad2d5f-621b-4b85-b296-5df2e80d9565_nhaxeTuLac.jpg'),(2,'BUS_PARTNER',1,'BUS_PARTNER','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/e7a6d1be-3357-4c02-9afa-6fb90c27f893_nhaxeTuLac2.jpg'),(3,'BUS_PARTNER',1,'BUS_PARTNER','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/1fbdde86-a9ca-42c5-8269-59027e219a55_nhaxeTuLac.jpg'),(4,'BUS',1,'BUS','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/56339568-2b08-4f62-9668-eb79c95c9822_anhxegiuongnam2.jpg'),(5,'BUS',2,'BUS','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/73b83efd-5432-4ab9-beac-cb97b2b735a0_anhxegiuongnam2.jpg'),(6,'BUS',3,'BUS','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/1b131995-0321-4202-819e-915ffc5614e2_anhxegiuongnam2.jpg'),(7,'BUS',4,'BUS','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/3aaedcea-7aa5-4e0b-ba92-0e51f28b0e90_anhxegiuongnam2.jpg'),(8,'BUS',5,'BUS','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/80c1f1dd-3f20-4016-9c2f-9a548c82c82e_anhxegiuongnam2.jpg'),(9,'BUS',6,'BUS','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/648a7341-e601-4e49-8ee4-f8204f0c3449_a1.jpg'),(10,'BUS',7,'BUS','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/7140ba4b-3238-4fee-b056-a5f913921975_a1.jpg');
/*!40000 ALTER TABLE `images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `order_bus_trip`
--

LOCK TABLES `order_bus_trip` WRITE;
/*!40000 ALTER TABLE `order_bus_trip` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_bus_trip` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (1,'2024-11-22 03:01:25.887271','fetch all accounts in system','GET_ALL_ACCOUNT',NULL),(2,'2024-11-22 03:01:25.908034','see all register form of business partner','GET_ALL_REGISTER_BUSINESS_PARTNER',NULL),(3,'2024-11-22 03:01:25.910720','see detail register form of business partner','VIEW_REGISTER_BUSINESS_PARTNER',NULL),(4,'2024-11-22 03:01:25.913339','confirm register business partner','VERIFY_REGISTER_BUSINESS_PARTNER',NULL),(5,'2024-11-22 03:01:25.915749','cancel partnership business partner','CANCEL_BUSINESS_PARTNER',NULL),(6,'2024-11-22 03:01:25.919485','see all register form of driver','GET_ALL_REGISTER_DRIVER',NULL),(7,'2024-11-22 03:01:25.922097','see detail register form of driver','VIEW_REGISTER_DRIVER',NULL),(8,'2024-11-22 03:01:25.924709','confirm register driver','VERIFY_REGISTER_DRIVER',NULL),(9,'2024-11-22 03:01:25.927002','cancel partnership driver','CANCEL_DRIVER',NULL);
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `permission_role`
--

LOCK TABLES `permission_role` WRITE;
/*!40000 ALTER TABLE `permission_role` DISABLE KEYS */;
INSERT INTO `permission_role` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9);
/*!40000 ALTER TABLE `permission_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `rating`
--

LOCK TABLES `rating` WRITE;
/*!40000 ALTER TABLE `rating` DISABLE KEYS */;
/*!40000 ALTER TABLE `rating` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'2024-11-22 03:01:25.794468','ADMIN',NULL),(2,'2024-11-22 03:01:25.852162','USER',NULL),(3,'2024-11-22 03:01:25.854937','BUS_PARTNER',NULL),(4,'2024-11-22 03:01:25.857485','CAR_RENTAL_PARTNER',NULL),(5,'2024-11-22 03:01:25.859753','DRIVER',NULL);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `utilities`
--

LOCK TABLES `utilities` WRITE;
/*!40000 ALTER TABLE `utilities` DISABLE KEYS */;
INSERT INTO `utilities` VALUES (1,'Trên xe có phục vụ thức ăn nhẹ.','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/f274a51e-5e30-46e7-8103-e6fb040dfb76_food2.png','Thức ăn'),(2,'Búa phá kính trong trường hợp khẩn cấp','https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/8da144e9-871f-46d2-a685-fddc959d42ee_buaphakinh.png','Búa');
/*!40000 ALTER TABLE `utilities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `vehicle_register`
--

LOCK TABLES `vehicle_register` WRITE;
/*!40000 ALTER TABLE `vehicle_register` DISABLE KEYS */;
/*!40000 ALTER TABLE `vehicle_register` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `vehicle_type`
--

LOCK TABLES `vehicle_type` WRITE;
/*!40000 ALTER TABLE `vehicle_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `vehicle_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `voucher`
--

LOCK TABLES `voucher` WRITE;
/*!40000 ALTER TABLE `voucher` DISABLE KEYS */;
/*!40000 ALTER TABLE `voucher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `voucher_account`
--

LOCK TABLES `voucher_account` WRITE;
/*!40000 ALTER TABLE `voucher_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `voucher_account` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-23 10:44:47
