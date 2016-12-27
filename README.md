问题管理 BUG管理 任务管理 项目管理系统Cynthia
=============================================
		Cynthia提供了一个基于Web的、开源的、跨平台的软件项目管理和问题管理的解决方案。通过极大的灵活度，实现了特殊性和普遍性的统一。
		它提供了强大的自定义配置功能，用户能够根据自己的实际情况配置问题的流转和数据模板，它同时内置了强大的Cache模块提高数据的访问效率。
为什么选择Cynthia
-----------------
		与其他系统相比Cynthia有以下这些优势：
		1.	巨大的灵活性，任何东西都是可以配置的，甚至包括整个流程、表单和人员角色，所以Cynthia不仅仅只用于问题管理，还可以用于需求管理、运维管理等各种工作流程系统
		2.	丰富的统计报表，可以根据不同类型的统计方式获取报表
		3.	强大的数据筛选功能，包括对任何字段及历史记录的筛选，并能够长期保存
		4.	详细的日志信息，对于每次修改系统都会详细的记录
		5.	数据标记功能，对于系统中的数据可以方便的打标记
		6.	丰富的快捷操作，在实际项目中总结出来的一些常用的批量操作，例如批量修改，批量导出等功能
		7.	方便部署，直接打war包部署到tomcat resin等web服务器上即可
		8.	简洁漂亮的UI，采用Google风格的样式，看起来比较清爽
		9.	开源的软件，用户团队可以方便的进行二次开发 
		10.	强大的技术支持，我们有专业的团队维护和定期升级系统，您的反馈我们都会认真评估并在后面的版本中发布
		11. 流程多人指派配置
		12. 表单动作邮件灵活配置
		…….

如何开始
--------
		本章主要介绍在一个全新的环境下如何开始使用Cynthia管理项目和问题，只是做一个简单的介绍，主要从流程定义，表单定义，用户管理和问题处理等步骤介绍，每个步骤的详情在后面的章节会进行介绍。
		1. 定义一个流程
		Cynthia是管理问题的工作流系统，问题由谁处理，处理完之后如何往下传递，整个流程里面都有哪些角色，这些问题Cynthia里面都没有规定死，而是交给用户根据实际情况去配置，有的小项目可能只有开发和测试，而有的大项目可能还有产品，运维，项目经理等各种复杂的角色，所以我们把权力交给用户，这样可能会增加配置时的复杂度，但是也更灵活了。开始时可以由管理员来创建流程。
		2. 定义一个表单
		测试人员在定义问题的时候需要填写一些字段，而这些字段对于不同的项目来说肯定也是不一样的，基于移动端的项目肯定要有移动终端的型号，移动操作系统或者网络状态等字段，而在PC端的项目来说这些字段则完全没有必要存在，因此我们也把定义字段的权利交给用户，用户针对不同的项目来定义不同的字段。
		3. 增加用户
		每个流程中都有定义的角色，这个也是用户根据不同的项目定义的，针对不同的角色用户可以添加不同的人员，这样项目之间和角色之间没有任何影响，权限也更清晰。
		4. 新建和处理问题
		
如何部署
--------
	Windows一键安装：
		v2.0安装包下载地址：http://pan.baidu.com/s/1dD3Y0k5
		
	手动部署：
		本单主要介绍如何部署cynthia服务(以tomcat为例)：
		1. 线上下载cynthia.war包  或者地址：http://git.oschina.net/cynthia/Cynthia/blob/master/cynthia.war
		2. 将下载的cynthia.war包  放在tomcat的webapps目录下面
		3. 新建数据库命名为cynthia，导入docs目录下的cynthia.sql文件 , 请在新建数据库和导入sql文件是务必选择utf-8编码！
		4. 启动tomcat应用,tomcat会自动解压Cynthia.war包 为Cynthia文件夹
		5. 打开webapps/Cynthia/WEB-INF/classes目录下的config.properties 根据docs目录下的cynthia参数配置说明进行配置。（必须配置driverClassName、url、username、password参数!!）
		6. 重启tomcat
		7. 打开网址 localhost:8080/Cynthia/index.html 即可访问! 默认系统管理员帐号:admin 密码:admin

如何升级
--------
	Windows版一键升级
		一键升级包下载地址：http://pan.baidu.com/s/1i3pg1NZ
	手动升级：
		1.关闭tomcat服务
		2.删除tomcat/webapps下Cynthia目录，下载最新的War包到webapps下
		3.重启tomcat服务 修改config.properties
		
环境说明
--------
		经测试，如下环境下正确运行。如有其它环境不能运行，请加群反馈我们~
		jdk: 1.6 1.7
		tomcat: 6&7 暂不支持tomcat8, 有些tomcat的版本有问题，我们测试7.0.53没有问题，大家部署的时候尽量去官网下载最新的7.x版本
		下载地址：http://tomcat.apache.org/download-70.cgi
		mysql 5.x
		
Demo 地址
--------
		http://opencynthia.duapp.com
		测试管理员账号 admin 密码：admin
		测试普通用户 test1@test.com;test2@test.com 密码：123456
文档下载地址
--------
http://git.oschina.net/cynthia/Cynthia/attach_files

Maven部署的一些问题解决方法
--------
1. tools.jar依赖问题
https://my.oschina.net/cloudcoder/blog/189560
2. jre依赖的问题
http://stackoverflow.com/questions/12585380/maven-unable-to-locate-the-javac-compiler-in

交流、反馈和建议
---------------
		QQ群：305330695

