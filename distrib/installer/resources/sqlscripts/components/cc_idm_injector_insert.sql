--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
INSERT INTO `resource` VALUES
    (21,'CC injector for directory common network','ccInjDirComNtw',1),
    (22,'CC injector for directory common system','ccInjDirComSys',0),
    (23,'CC injector for directory common organisation','ccInjDirComOrg',2);
UNLOCK TABLES;



--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
INSERT INTO `permission` VALUES
    (70,'can display CC injector for directory common network','ccInjDirComNtw:display',3,21),
    (71,'can display CC injector for directory common system','ccInjDirComSys:display',3,22),
    (72,'can display CC injector for directory common organisation','ccInjDirComOrg:display',3,23);
UNLOCK TABLES;



--
-- Dumping data for table `resource_permission`
--

LOCK TABLES `resource_permission` WRITE;
INSERT INTO `resource_permission` VALUES
    (21,70),
    (22,71),
    (23,72);
UNLOCK TABLES;



--
-- Dumping data for table `permission_role`
--

LOCK TABLES `permission_role` WRITE;
INSERT INTO `permission_role` VALUES
   (70,7),(70,1),(70,13),
   (71,8),(71,1),(71,14),
   (72,9),(72,1),(72,15);
UNLOCK TABLES;



--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
INSERT INTO `role_permission` VALUES
    (1,70),(1,71),(1,72),
    (7,70),
    (8,71),
    (9,72),
    (13,70),
    (14,71),
    (15,72);
UNLOCK TABLES;