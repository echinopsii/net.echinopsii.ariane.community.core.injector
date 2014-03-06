--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
INSERT IGNORE INTO `resource` (description, resourceName, version) VALUES
    ('CC injector for directory common network','ccInjDirComNtw',1),
    ('CC injector for directory common system','ccInjDirComSys',1),
    ('CC injector for directory common organisation','ccInjDirComOrg',1);
UNLOCK TABLES;



--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE,`resource` WRITE;
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC injector for directory common network', 'ccInjDirComNtw:display', 1, id FROM resource WHERE resourceName='ccInjDirComNtw';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC injector for directory common system', 'ccInjDirComSys:display', 1, id FROM resource WHERE resourceName='ccInjDirComSys';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC injector for directory common organisation', 'ccInjDirComOrg:display', 1, id FROM resource WHERE resourceName='ccInjDirComOrg';
UNLOCK TABLES;



--
-- Dumping data for table `resource_permission`
--

LOCK TABLES `resource_permission` WRITE,`permission` AS p WRITE,`resource` AS r WRITE ;
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccInjDirComNtw' AND p.permissionName='ccInjDirComNtw:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccInjDirComSys' AND p.permissionName='ccInjDirComSys:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccInjDirComOrg' AND p.permissionName='ccInjDirComOrg:display';
UNLOCK TABLES;



--
-- Dumping data for table `permission_role`
--

LOCK TABLES `permission_role` WRITE,`permission` AS p WRITE,`role` AS r WRITE;
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComNtw:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComNtw:display' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComNtw:display' AND r.roleName='ccntwreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComSys:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComSys:display' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComSys:display' AND r.roleName='ccsysreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComOrg:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComOrg:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComOrg:display' AND r.roleName='ccorgreviewer';
UNLOCK TABLES;



--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE,`permission` AS p WRITE,`role` AS r WRITE;
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComNtw:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComSys:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComOrg:display' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComNtw:display' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComSys:display' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComOrg:display' AND r.roleName='ccntwadmin';

SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComNtw:display' AND r.roleName='ccntwreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComSys:display' AND r.roleName='ccntwreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComOrg:display' AND r.roleName='ccntwreviewer';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComNtw:display' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComSys:display' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComOrg:display' AND r.roleName='ccsysadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComNtw:display' AND r.roleName='ccsysreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComSys:display' AND r.roleName='ccsysreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComOrg:display' AND r.roleName='ccsysreviewer';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComNtw:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComSys:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComOrg:display' AND r.roleName='ccorgadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComNtw:display' AND r.roleName='ccorgreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComSys:display' AND r.roleName='ccorgreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccInjDirComOrg:display' AND r.roleName='ccorgreviewer';
UNLOCK TABLES;