<%@page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page language="java" pageEncoding="UTF-8"%>

<%@ include file="filter.header.functions.jsp"%>

<%@ page import="java.sql.Timestamp"%>

<%
	// 准备变量
Filter filter = null;
Document document = null;
Element channel = null;

Data[][] taskArrays = null;
Filter[] filters = null;

long kid = DataAccessFactory.magic;
String username = request.getParameter("username");

//检查登录状态
if (session.getAttribute( "kid" ) != null)
	kid = (Long)session.getAttribute( "kid" );
if(session.getAttribute("key") != null)
	username = ((Key)session.getAttribute("key")).getUsername();

if (kid <= 0 || username == null)
{
	out.clear();
	out.print(createErrorResultXml("参数错误", "参数错误", ConfigUtil.getCynthiaWebRoot() + "search/filterManagement.jsp"));

	response.setContentType("text/xml; charset=UTF-8");

	return;
}

String sort = request.getParameter("sort");
String dir = request.getParameter("dir");
String forMailSend = request.getParameter("mail"); //如果是发送邮件则不会限制筛选结果的数量 add by liu yanlei

UUID templateTypeId = null;
UUID templateId = null;
UUID filterId = null;

// int pagenum = 1;
// int count = 10000;

int pagenum = 0;
int count = 0;

UUID orderTemplateId = null;

HashSet<String>	notNewTaskIdSet	= new HashSet<String>();

int orderDisplayIndex = -1;
int tasksCount = 0;
int maxTasksCount = 0;
int typesCount = 0;
int filterTaskAccount = 0;
int totalTaskAccount = 0;

boolean isDesc = false;

Map<String, String> userAliasMap = new HashMap<String, String>();

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(username, kid);

List<Data> dataList = null;
String[] dataIds = request.getParameterValues("dataIds");
if (dataIds == null)
{
	String filterIdStr = request.getParameter("filterId");
	if (filterIdStr != null)
		filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);

	String templateIdStr = request.getParameter("template");
	if (templateIdStr != null)
		templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);

	boolean isSession = false;
	if( request.getParameter( "isSession" ) != null )
		isSession = Boolean.parseBoolean( request.getParameter( "isSession" ) );

	if( isSession && session.getAttribute( "taskFilterBean" + ( filterIdStr == null?"":filterIdStr ) ) != null )
	{
		if( filterIdStr == null )
			filter = (Filter)session.getAttribute( "taskFilterBean" );
		else
			filter = (Filter)session.getAttribute( "taskFilterBean" + filterIdStr );
	}
	else if( filterId != null )
	{
		filter = das.queryFilter(filterId);
		if (filter == null)
		{
			out.clear();
			out.print(createErrorResultXml("筛选器不存在", "筛选器不存在", ConfigUtil.getCynthiaWebRoot() + "search/filterManagement.jsp"));
			response.setContentType("text/xml; charset=UTF-8");
			return;
		}
	}
	else if ( request.getParameter("filterXml") != null )
	{
		String filterName = request.getParameter("filterName");
		Timestamp createTime = new Timestamp(System.currentTimeMillis());
		String filerXml = request.getParameter("filterXml");

		filter = das.createTempFilter(username, createTime, null);
		filter.setName(filterName);
		filter.setXml(filerXml);
		filter.setAnd(false);
		filter.setPublic(false);
		filter.setForce(false);

		if( filterIdStr == null )
			session.setAttribute( "taskFilterBean", filter );
		else
			session.setAttribute( "taskFilterBean" + filterIdStr, filter );
	}
	else
	{
		filter = (Filter)session.getAttribute( "taskFilterBean" );
	}

	if( filter == null )
	{
		out.clear();
		out.print(createErrorResultXml("没有选定筛选器", "您没有选定筛选器，请选定筛选器", ConfigUtil.getCynthiaWebRoot() + "search/filterManagement.jsp"));

		response.setContentType("text/xml; charset=UTF-8");

		return;
	}

	// 检查重构分组
	if( request.getParameter( "orderDisplayIndex" ) != null )
	{
		String orderTemplateIdStr = request.getParameter( "orderTemplateId" );
		if(orderTemplateIdStr != null && orderTemplateIdStr.length() > 0)
			orderTemplateId = DataAccessFactory.getInstance().createUUID( orderTemplateIdStr );

		orderDisplayIndex = Integer.parseInt( request.getParameter( "orderDisplayIndex" ) );

		Document	xmlDoc	= XMLUtil.string2Document(filter.getXml(), "UTF-8");

		List<Node>	tempNodeList	= XMLUtil.getNodes(xmlDoc,"/query/templateType/display");
		Element displayNode = null;
		Element orderNode = null;

		// 约束在表单类型上
		if( tempNodeList != null && !tempNodeList.isEmpty() )
		{
			displayNode = ( Element )tempNodeList.get( 0 );
			orderNode = ( Element )XMLUtil.getNodes(xmlDoc,"/query/templateType/order").get( 0 );
		}
		else
		{
			tempNodeList = XMLUtil.getNodes( xmlDoc, "/query/template" );
			for( int i = 0; i < tempNodeList.size(); i++ )
			{
				if( tempNodeList.get( i ).getNodeType() != Node.ELEMENT_NODE )
					continue;

				Element tempNode = ( Element )tempNodeList.get( i );
				if( DataAccessFactory.getInstance().createUUID( tempNode.getAttribute( "id" ) ).equals(orderTemplateId ))
				{
					displayNode = ( Element )XMLUtil.getNodes( tempNode, "/template/display" ).get( 0 );
					orderNode = ( Element )XMLUtil.getNodes( tempNode, "/template/order" ).get( 0 );

					break;
				}
			}
		}

		int tempIndex = orderDisplayIndex;

		// 如果确实是需要重新分组的
		if( displayNode != null && orderNode != null )
		{

			boolean changeOrder = true;
			if(request.getParameter("changeOrder") != null)
			{
				try
				{
					changeOrder = Boolean.parseBoolean(request.getParameter("changeOrder"));
				}
				catch(Exception e){}
			}

			int indent = 0;

			if( orderNode.getAttribute( "indent" ) != null && !"".equals( orderNode.getAttribute( "indent" ) ) )
				indent = Integer.parseInt( orderNode.getAttribute( "indent" ) );

			Element reOrderField = null;

			for( int j = 0; j < displayNode.getChildNodes().getLength(); j++ )
			{
				if( displayNode.getChildNodes().item( j ).getNodeType() != Node.ELEMENT_NODE )
					continue;

				if( tempIndex == 0 )
				{
					reOrderField = ( Element )displayNode.getChildNodes().item( j ).cloneNode( true );
					break;
				}
				else
				{
					tempIndex--;
				}
			}

			reOrderField.setAttribute( "desc", "false" );

			boolean isInsert = false;
			NodeList orderFieldNodeList = orderNode.getChildNodes();
			if( orderFieldNodeList != null && orderFieldNodeList.getLength() > 0 )
			{
				int txtNodeAccount = 0;
				for( int i = 0; i < orderFieldNodeList.getLength(); i++ )
				{
					if( orderFieldNodeList.item( i ).getNodeType() != Node.ELEMENT_NODE )
					{
						txtNodeAccount++;

						if( ( i == orderFieldNodeList.getLength() - 1 ) && !isInsert )
						{
							orderNode.insertBefore( reOrderField, null );

							isInsert = true;
						}

						continue;
					}

					Element tempNode = (Element)orderFieldNodeList.item( i );

					if( tempNode.getAttribute( "id" ).equals( reOrderField.getAttribute( "id" ) ) )
					{
						// 分组字段，或者非分组字段的第一个
						if( i <= ( indent + txtNodeAccount ) )
						{
							if(changeOrder)
								isDesc = !Boolean.parseBoolean( tempNode.getAttribute( "desc") );
							else
								isDesc = Boolean.parseBoolean( tempNode.getAttribute( "desc") );

							tempNode.setAttribute( "desc", String.valueOf( isDesc ) );

							isInsert = true;
						}
						else
						{
							// 将后出现的移除，因为已经在他之前插进了一个相同fileId的NODE
							orderNode.removeChild( tempNode );
						}

						break;
					}
					else if( i == ( indent + txtNodeAccount ) && !isInsert )
					{
						// 一直过了分组字段仍然也没有找到，则插在非分组字段的最前端
						orderNode.insertBefore( reOrderField, orderFieldNodeList.item( i ) );
						isInsert = true;
					}
					else if( i == orderFieldNodeList.getLength() - 1 && !isInsert )
					{
						orderNode.insertBefore( reOrderField, null );
						isInsert = true;
					}
				}
			}
			else
			{
				orderNode.appendChild( reOrderField );
			}

			filter.setXml( XMLUtil.document2String(xmlDoc, "UTF-8") );
		}
	}


	// 试图获取页号参数
	if (request.getParameter("page") != null)
		pagenum = Integer.parseInt(request.getParameter("page"));

	// 试图获取每页数量参数
	if (request.getParameter("count") != null)
		count = Integer.parseInt(request.getParameter("count"));

	int _start = -1;
	int _limit = -1;

	if (request.getParameter("start") != null)
		_start = Integer.parseInt(request.getParameter("start"));

	if (request.getParameter("limit") != null)
		_limit = Integer.parseInt(request.getParameter("limit"));

	if (_start != -1 && _limit != -1)
	{
		pagenum = (_start / _limit) + 1;
		count = _limit;
	}

	if(filter == null)
		return;

	//试图获取表单类型参数
	if (request.getParameter("type") != null && request.getParameter("type").trim().length() > 0)
		templateTypeId = DataAccessFactory.getInstance().createUUID(request.getParameter("type").trim());

	{
		// 生成筛选器XML Dom
		Document filterDocument = XMLUtil.string2Document( filter.getXml(), "UTF-8" );
		Element templateTypeNode = (Element)filterDocument.getElementsByTagName( "templateType" ).item( 0 );
		long filterStartTime = System.currentTimeMillis();
		System.out.println("start filter:"+filter.getId() + "userName:" + username);
		// 如果表单类型是环境变量
		if( !"$current_template_type$".equals( templateTypeNode.getAttribute( "id" ) ) )
		{
			// 初始化环境变量
			initFilterEnv(filter, kid, username, request, templateId, das);
			filters = new Filter[1];
			filters[0] = filter;

			// 查询Task数组
			taskArrays = new Data[1][];
			if("true".equals(forMailSend)){
				taskArrays[0] = das.getDataFilter().queryDatas(filter.getXml(),null);
			}
			else if(sort != null && dir != null){
				taskArrays[0] = das.getDataFilter().queryDatas(filter.getXml(), pagenum, count, sort,dir,null);
			}
			else{
				taskArrays[0] = das.getDataFilter().queryDatas(filter.getXml(), pagenum, count,null);
			}

			tasksCount += taskArrays[0].length;
			if (taskArrays[0].length > maxTasksCount)
				maxTasksCount = taskArrays[0].length;

			typesCount ++;
		}
		else
		{
			Set<TemplateType> templateTypeSet = new LinkedHashSet<TemplateType>();
			if(templateTypeId != null)
			{
				TemplateType templateType = das.queryTemplateType(templateTypeId);
				if(templateType != null)
					templateTypeSet.add(templateType);
			}
			else
			{
				TemplateType[] templateTypeArray = das.queryAllTemplateTypes();
				if(templateTypeArray != null && templateTypeArray.length > 0)
					templateTypeSet.addAll(Arrays.asList(templateTypeArray));
			}

			List<Filter> filterList = new ArrayList<Filter>(templateTypeSet.size());
			List<Data[]> tasksList = new ArrayList<Data[]>(templateTypeSet.size());

			for (TemplateType type : templateTypeSet)
			{
				Filter tempFilter = das.createTempFilter(filter.getCreateUser(), filter.getCreateTime(), filter.getFatherId());
				tempFilter.setName(filter.getName());
				tempFilter.setXml(filter.getXml());
				tempFilter.setAnd(filter.isAnd());
				tempFilter.setPublic(filter.isPublic());
				tempFilter.setForce(filter.isForce());

				initFilterEnv(tempFilter, kid, username, request, templateId, das);
				filterList.add(tempFilter);


				Data[] tempTasks = new Data[0];
				if("true".equals(forMailSend)){
					taskArrays[0] = das.getDataFilter().queryDatas(tempFilter.getXml(),null);
				}else if(sort != null && dir != null){
					tempTasks = das.getDataFilter().queryDatas(tempFilter.getXml(), pagenum, count,sort,dir,null);
				}else{
					tempTasks = das.getDataFilter().queryDatas(tempFilter.getXml(), pagenum, count,null);
				}

				tasksList.add(tempTasks);

				tasksCount += tempTasks.length;
				if (tempTasks.length > maxTasksCount)
					maxTasksCount = tempTasks.length;

				typesCount ++;
			}

			filters = filterList.toArray(new Filter[0]);
			taskArrays = tasksList.toArray(new Data[0][]);
		}
		long filterEndTime = System.currentTimeMillis();
		long spendTime = filterEndTime - filterStartTime;
		System.out.println(username+":"+filter.getId()+": filter end spend time: "+spendTime);
	}

	//查询newTask
	try{
		if (filterId != null)
		{
			UUID[]	filterIdArray	= new UUID[]{filterId};
			//query new/total taskCount
// 			String	filterParamXml	= ConfigUtil.getEnvXML(username, templateTypeId, templateId, das);
			String	xmlString	= das.getNewTaskIdsByFilterAndUser(filterIdArray, username);

			//parse resultCount
			Document	xmlDoc	= XMLUtil.string2Document(xmlString, "UTF-8");
			Node filterNode = XMLUtil.getNodes(xmlDoc,"filters/filter").get(0);
			String oldIdStrs = XMLUtil.getSingleNodeTextContent(filterNode,"oldTasks");

			if(templateId == null){
				filterTaskAccount = Integer.parseInt(XMLUtil.getAttribute(filterNode, "maxAccount"));
				totalTaskAccount = Integer.parseInt(XMLUtil.getAttribute(filterNode, "totalAccount"));

				if(oldIdStrs != null){
					String[] oldIdStrArray = oldIdStrs.split(",");
					notNewTaskIdSet.addAll(Arrays.asList(oldIdStrArray));
				}
			}
			else{
				if(oldIdStrs != null){
					String[] oldIdStrArray = oldIdStrs.split(",");
					for(String oldIdStr : oldIdStrArray){
						UUID dataId = DataAccessFactory.getInstance().createUUID(Long.valueOf(oldIdStr, 36).toString());
						Data data = das.queryData(dataId);
						if(data != null && data.getTemplateId().equals(templateId)){
							totalTaskAccount++;
							notNewTaskIdSet.add(oldIdStr);
						}
					}
				}
				String newIdStrs = XMLUtil.getSingleNodeTextContent(filterNode,"newTasks");
				if(newIdStrs != null){
					String[] newIdStrArray = newIdStrs.split(",");
					for(String newIdStr : newIdStrArray){
						UUID dataId = DataAccessFactory.getInstance().createUUID(Long.valueOf(newIdStr, 36).toString());
						Data data = das.queryData(dataId);
						if(data != null && data.getTemplateId().equals(templateId)){
							totalTaskAccount++;
						}
					}
				}

				filterTaskAccount = totalTaskAccount;
			}
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}
else
{
	dataList = new ArrayList<Data>(dataIds.length);
	for (String dataIdStr : dataIds)
	{
		UUID dataId = DataAccessFactory.getInstance().createUUID(dataIdStr);
		Data data = das.queryData(dataId);
		if (data != null)
		dataList.add(data);
	}

	tasksCount = dataList.size();
	maxTasksCount = dataList.size();
	typesCount = 1;
	filterTaskAccount = dataList.size();
	totalTaskAccount = dataList.size();
}

boolean isRSS = (request.getParameter("rss") != null && request.getParameter("rss").equals("true"));
boolean isFlat = (request.getParameter("flat") != null && request.getParameter("flat").equals("true")) || isRSS;
boolean isCompact = (request.getParameter("compact") != null && request.getParameter("compact").equals("true"));
boolean isXML = (request.getParameter("xml") != null && request.getParameter("xml").equals("true")) || isRSS || isFlat || isCompact;
boolean isPlain = (request.getParameter("plain") != null && request.getParameter("plain").equals("true"));
boolean needMenu = (dataIds == null) && !isCompact && isXML && (request.getParameter("menu") != null && request.getParameter("menu").equals("true"));
boolean needItemHtml = !isCompact && isXML && (request.getParameter("itemhtml") != null && request.getParameter("itemhtml").equals("true"));
boolean needPageturning = (dataIds == null) && !isCompact && isXML && ((request.getParameter("pageturning") == null || (request.getParameter("pageturning") != null && !request.getParameter("pageturning").equals("false")))) || needItemHtml;
boolean needUrl = !isCompact && isXML && ((request.getParameter("url") == null || (request.getParameter("url") != null && !request.getParameter("url").equals("false")))) || needItemHtml;

String title = (dataIds == null) ? filter.getName() : "来自指定的数据ID";
String feed = (dataIds == null) ? "" : request.getRequestURL().toString() + "?rss=true&itemhtml=true&menu=true" + createParameterString(request.getParameterMap(), new String[]{"k", "kid", "rss", "xml", "itemhtml", "menu"});
// String menu = needMenu ? ConfigUtil.getHeadString(username, templateTypeId, tasksCount, das) : "";

//组织结果的XML文档
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
DocumentBuilder builder = factory.newDocumentBuilder();

// 初始化Document
document = builder.newDocument();

if (isXML)
{
	// 设置rss元素
	Element rss = null;
	if (isRSS)
	{
		//	 设置stylesheet
		if (needItemHtml)
		{
			ProcessingInstruction pi = document.createProcessingInstruction("xml-stylesheet", "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" href=\"../css/filter.rss.xsl\" type=\"text/xsl\" media=\"screen\"");
			document.appendChild(pi);
		}

		rss = document.createElement("rss");
		rss.setAttribute("version", "2.0");
		document.appendChild( rss );
	}
	else if (needItemHtml)
	{
		//	 设置stylesheet
		ProcessingInstruction pi = document.createProcessingInstruction("xml-stylesheet", "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" href=\"../css/filter.xsl\" type=\"text/xsl\" media=\"screen\"");
		document.appendChild(pi);
	}

	   // 设置channel元素
	channel = document.createElement(isRSS ? "channel" : "resultset");
	{
		Element element = null;

		if (dataIds != null)
		{
			element = document.createElement("uuid");
			element.setTextContent(filter.getId().toString());
			channel.appendChild(element);
		}

		element = document.createElement("title");
		element.setTextContent(isPlain ? title : XMLUtil.toSafeXMLString(title));
		channel.appendChild(element);

		if (!isCompact)
		{
			element = document.createElement("description");
			element.setTextContent(isPlain ? title : XMLUtil.toSafeXMLString(title));
			channel.appendChild(element);

			if (needUrl)
			{
				element = document.createElement("feedurl");
				element.setTextContent(feed);
				channel.appendChild(element);

				element = document.createElement("link");
				element.setTextContent(request.getRequestURL().toString() + "?" + createParameterString(request.getParameterMap(), new String[]{"k", "kid", "rss"}).substring(1));
				channel.appendChild(element);
			}

			element = document.createElement("page");
			element.setTextContent(Integer.toString(pagenum));
			channel.appendChild(element);

			element = document.createElement("totalpage");
			element.setTextContent(Integer.toString(getTotalPage(pagenum, count, maxTasksCount, filterTaskAccount)));
			channel.appendChild(element);

			element = document.createElement("countperpage");
			element.setTextContent(Integer.toString(count));
			channel.appendChild(element);

			element = document.createElement("itemcount");
			element.setTextContent(Integer.toString(tasksCount));
			channel.appendChild(element);
		}

		element = document.createElement("totalitemcount");
		element.setTextContent(Integer.toString(totalTaskAccount));
		channel.appendChild(element);

		if (!isCompact)
		{
			if (needPageturning)
			{
				element = document.createElement("pageturning");
				element.setTextContent(isPlain ? "" : XMLUtil.toSafeXMLString(""));
				channel.appendChild(element);
			}

			element = document.createElement("pagenum");
			element.setTextContent(Integer.toString(pagenum));
			channel.appendChild(element);

			element = document.createElement("islast");
			element.setTextContent(Boolean.toString(!(maxTasksCount >= count)));
			channel.appendChild(element);

		}
	}

	if (isRSS)
		rss.appendChild(channel);
	else
		document.appendChild(channel);
}
%>
