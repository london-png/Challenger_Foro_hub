-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: vollmed_forohub
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `curso`
--

DROP TABLE IF EXISTS `curso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `curso` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `categoria` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `curso`
--

LOCK TABLES `curso` WRITE;
/*!40000 ALTER TABLE `curso` DISABLE KEYS */;
INSERT INTO `curso` VALUES (1,'Drones','Vehículos Aéreos No Tripulados'),(2,'Seguridad','Directorio Activo'),(3,'Soporte mesa de  de ayuda','Conexiones'),(4,'Espacio File System','Datacenter'),(5,'Cableado Lan','Redes');
/*!40000 ALTER TABLE `curso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','create-table-usuarios','SQL','V1__create-table-usuarios.sql',1345173984,'root','2026-02-03 03:28:05',36,1),(2,'2','create-table-curso','SQL','V2__create-table-curso.sql',1060404171,'root','2026-02-03 03:28:05',31,1),(3,'3','create-table-topico','SQL','V3__create-table-topico.sql',-657940083,'root','2026-02-03 03:28:05',119,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `respuesta`
--

DROP TABLE IF EXISTS `respuesta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `respuesta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `autor` varchar(255) DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `mensaje` varchar(255) DEFAULT NULL,
  `solucion` bit(1) NOT NULL,
  `topico_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjra4s6fhmvi5bm7y30a42pmv0` (`topico_id`),
  CONSTRAINT `FKjra4s6fhmvi5bm7y30a42pmv0` FOREIGN KEY (`topico_id`) REFERENCES `topico` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `respuesta`
--

LOCK TABLES `respuesta` WRITE;
/*!40000 ALTER TABLE `respuesta` DISABLE KEYS */;
INSERT INTO `respuesta` VALUES (1,_binary '','Carlos Palacios','2026-02-02 22:50:46.603248','Denben actualizar la Alu del drone dar en inicio y seguir las insstrucciones que se muestran en la pantalla del controlol',_binary '',1),(2,_binary '','Carlos Palacios','2026-02-03 23:43:15.265949','Valide que su equipo tenga activo la opcion de bluetooth y que la version de bluetooth sea compatible con sus Speakers y su equipo l que quiere conectarlo',_binary '',5),(3,_binary '','Carlos Palacios','2026-02-05 23:37:02.320976','Debe validar con la persona de soporte que valide que el cable UTP se encuentre bien ponchado y que el plugin de conexion se encuentre en optimas consiciones',_binary '',7);
/*!40000 ALTER TABLE `respuesta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `topico`
--

DROP TABLE IF EXISTS `topico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `topico` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `titulo` varchar(255) NOT NULL,
  `mensaje` varchar(255) DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL,
  `status` varchar(20) NOT NULL,
  `autor` varchar(255) NOT NULL,
  `curso_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1tte37u6mcyrfdwnfdyn2vgo1` (`titulo`,`mensaje`),
  KEY `curso_id` (`curso_id`),
  CONSTRAINT `topico_ibfk_1` FOREIGN KEY (`curso_id`) REFERENCES `curso` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topico`
--

LOCK TABLES `topico` WRITE;
/*!40000 ALTER TABLE `topico` DISABLE KEYS */;
INSERT INTO `topico` VALUES (1,1,'Error en la Alu','No se mueven las helices del drone','2026-02-02 22:48:04','RESUELTO','Angie Londoño',1),(3,1,'No se muestran satelites disponibles','Muevase a un lugar despejado que no se tape el cielo','2023-02-02 22:48:04','ABIERTO','Diana Vargas',1),(4,0,'No puedo ingresar al servidor de Anteo','No tiene permisis en este servidor para si ingreso','2026-02-03 23:20:05','ABIERTO','Claudia Poveda',2),(5,1,'No conectan los speakers por medio del bluetooh','Valide que tenga el switch del bluetooth activo','2026-02-03 23:40:08','RESUELTO','Gloria Vargas',3),(6,1,'File System tool_3 se encuentraen un 99% ','no se puede almacenar en el file system tool_3','2026-02-05 02:15:11','ABIERTO','Paola Turbay',4),(7,1,'No tiene conexion a la red ','Cable utp desconectado, por favor validar','2026-02-05 23:34:26','RESUELTO','Cristina Hurtado',5);
/*!40000 ALTER TABLE `topico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `login` varchar(255) DEFAULT NULL,
  `contrasena` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'carlospalacios@voll.med','$2a$10$kVgRr5NfkiQxRpU/uueF3OcqNfGzyTyLu7nIwnw2Sor.ZQLUJD2/C');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-06  0:36:05
