package com.sogou.qadev.service.cynthia.service;

import java.util.ArrayList;
import java.util.List;

import bsh.EvalError;
import bsh.Interpreter;

import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.ExecuteTime;
import com.sogou.qadev.service.cynthia.bean.Script;
import com.sogou.qadev.service.cynthia.bean.Single;

/**
 * @description:script execute manger
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午6:29:59
 * @version:v1.0
 */
public class ScriptExecuteManager
{
	public static List<Interpreter> allDeamonScript = new ArrayList<Interpreter>();

	/**
	 * @description:add script to deamon list
	 * @date:2014-5-6 下午6:30:14
	 * @version:v1.0
	 * @param i
	 */
	public static synchronized void AddDeamonScript(Interpreter i){
		allDeamonScript.add(i);
	}
	
	private final static String RESULT_PREFIX_FAIL = "[fail]";

	private final static String RESULT_PREFIX_SUCCESS = "[success]";

	private static class SingletonHolder{
		private static ScriptExecuteManager scriptExecuteManager = new ScriptExecuteManager();
	}
	
	public static ScriptExecuteManager getInstance()
	{
		return SingletonHolder.scriptExecuteManager;
	}

	private ScriptExecuteManager()
	{
		super();
	}

	/**
	 * @description:execute script
	 * @date:2014-5-6 下午6:30:32
	 * @version:v1.0
	 * @param scriptArray
	 * @param data
	 * @param dataAccessSession
	 * @param scriptAccessSession
	 * @param continueable
	 * @param time
	 * @return
	 */
	public String execute(Script[] scriptArray, Data data, DataAccessSession dataAccessSession, ScriptAccessSession scriptAccessSession, Single<Boolean> continueable,
			ExecuteTime time)
	{
		if (continueable != null)
			continueable.setFirst(true);

		StringBuffer xmlStrb = new StringBuffer();

		for (Script script : scriptArray)
		{
			Interpreter bsh = new Interpreter();
			bsh.setClassLoader(this.getClass().getClassLoader());

			try
			{
				bsh.set("das", dataAccessSession);
				bsh.set("script", script);
				bsh.set("data", data);
				bsh.set("runTime", time);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				MailSender mailSender = new MailSender();
				mailSender.sendMail("cynthia 脚本执行错误!", "scriptId"+script.getId(), "liuyanlei@sogou-inc.com");
			}

			if (script.isAsync())
			{
				AddDeamonScript(bsh);
			}
			else
			{
				String result = (String)execute_i(bsh);

				if (result.startsWith(RESULT_PREFIX_FAIL) && continueable != null)
					continueable.setFirst(false);

				xmlStrb.append(makeXMLDoc(script, time, result));
			}
		}

		return xmlStrb.toString();
	}

	protected String makeXMLDoc(Script script, ExecuteTime time, String result)
	{
		StringBuffer strb = new StringBuffer();
		strb.append("<runTime scriptId=\"").append(script.getId()).append("\" type=\"");

		if (time == ExecuteTime.beforeCommit)
			strb.append("beforeCommit");
		else if (time == ExecuteTime.afterSuccess)
			strb.append("afterCommitSuccessed");
		else if (time == ExecuteTime.afterFail)
			strb.append("afterCommitFail");
		else if (time == ExecuteTime.afterQuery)
			strb.append("afterQueryTask");
		strb.append("\" success=\"").append(result.startsWith(RESULT_PREFIX_SUCCESS)).append("\">");

		if (result.startsWith(RESULT_PREFIX_SUCCESS))
			strb.append(result.substring(RESULT_PREFIX_SUCCESS.length()));
		else
			strb.append(result.substring(RESULT_PREFIX_FAIL.length()));

		strb.append("</runTime>");

		return strb.toString();
	}

	protected Object execute_i(Interpreter bsh)
	{
		String content = "";
		try
		{
			Script script = (Script) bsh.get("script");
			if (script == null)
				return null;

			content = script.getScript();
			if (content == null)
				return null;
			return bsh.eval(content);
		}
		catch (EvalError e)
		{
			System.out.println("script content:" + content);
			e.printStackTrace();
		}

		return null;
	}

}
