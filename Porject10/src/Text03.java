jsp

	<div class="col-3" style="width: 230px;">
				<input type="hidden" class="tab3-4params" style="width: 120px;" name="policyNo" value="${policyNo}">
				<span class="title" style="width: auto;">姓名：</span>
				<input type="text" class="tab3-4params" style="width: 120px;" name="insured" value="${insured}" maxlength="12" />
			</div>
			
			<input type="button" name="button" id="searchInfo3-4" value="查詢" />
			
			
	$("#searchInfo3-4").click(function() {
				$("#selectedTable tbody").empty();
				var formData = getTagData($(".tab3-4params"));
				if (formData.rangeEnd - formData.rangeBegin >= 500) {
					alert("請縮小查詢範圍在500內")
					return;
				}
				formData["policyNo"] = "${policyNo}";
				gotoPageTab4("<fg:url value='/gr/groupDetail01Tab3Info4.action' />", formData);
			});			

struts.xml

	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE struts PUBLIC "-//Apache Software Foundation//DTD Struts Configuration 2.5//EN"
		"http://struts.apache.org/dtds/struts-2.5.dtd">
	<struts>
		<!-- 開發模式，在IE會顯示詳細的錯誤(Stack Trace)上線後改為false -->
		<constant name="struts.devMode" value="false" />
		<constant name="struts.enable.DynamicMethodInvocation" value="true" />
		<constant name="struts.action.extension" value="action" />
		<constant name="struts.ui.theme" value="simple" />
		<constant name="struts.objectFactory" value="spring"/>
		<constant name="struts.i18n.encoding" value="UTF-8"/>	
		<constant name="struts.custom.i18n.resources" value="com.fg.website.resource.ApplicationResources" />
		<constant name="struts.ui.theme" value="simple" />
		<constant name="struts.ui.templateDir" value="template" />	
		<constant name="struts.multipart.saveDir" value="d:/logs"/>
		<!-- 專區 --> 
		<include file="struts-gr.xml"/>

	</struts>			
			
			
struts-gr.xml

		
		<action name="groupDetail01Tab3Info4" method="query01Tab3Info4" class="com.fg.website.gr.group.action.GroupInsuranceAction">
			<result name="success">/gr/pages/group/groupDetail01Info3_4.jsp</result>
			<interceptor-ref name="appDefaultStack" />
			<interceptor-ref name="sessionout" />
		</action>
		
tiles.xml
	<?xml  version="1.0" encoding="UTF-8" ?>

	<!DOCTYPE tiles-definitions PUBLIC
		   "-//Apache Software Foundation//DTD Tiles Configuration 3.0//EN"  
		   "http://tiles.apache.org/dtds/tiles-config_3_0.dtd">  
		   

	<tiles-definitions>
		<!--  -->
		<definition name="GrLayout" template="/gr/pages/group/layout/groupLayout.jsp">
			<put-attribute name="title" value="" />
			<put-attribute name="keyword" value="," />
			<put-attribute name="description" value="。" />
			<put-attribute name="script" value="/gr/pages/group/layout/groupScript.jsp" />
			<put-attribute name="footer" value="/gr/pages/Layout/footer.jsp" />
		</definition>

	</tiles-definitions>
	

GroupInsuranceAction
	/*
	 * 查詢
	 * 
	 * @return
	 */
	public String query01Tab3Info4() {
		try {
			String sessionId = "queryInsured0134";
			viewAllParams(bean);
			
			//分單位(輸入)
			String subUnitText4 = StringUtils.trimToEmpty(getRequestParameter("subUnitText4"));
			//要保單位顯示(選取)
			String checkRedio = "";
			if("1".equals(getRequestParameter("policyName"))) {
				checkRedio = StringUtils.trimToEmpty(getRequestParameter("policyName"));
			}else {
				checkRedio = StringUtils.trimToEmpty(getRequestParameter("companyName"));
			}
			
			//下拉選擇:空白、全部、無單位
			String type ="";
			if(StringUtils.isNotBlank(bean.getSubUnit4())){
				if(bean.getSubUnit4().equals("全部")) {
					type ="2";
					bean.setSubUnit("*");
				}else if(bean.getSubUnit4().equals("無單位")) {
					type ="3";
					bean.setSubUnit("");
				}else {
					bean.setSubUnit(bean.getSubUnit4());
				}
			}else {
				type ="1";
				bean.setSubUnit("*");
			}
			
			if(!subUnitText4.equals("")) {
				bean.setSubUnit(subUnitText4);
			}			
			logger.info("bean.getSubUnit()="+bean.getSubUnit());
			
			// 分單位(下拉選單)
			WGRA00DS wgra00ds = createVO(WGRA00DS.class, ActionType.QUERY);
			bean.setUnitList(loadWGRD04(wgra00ds));
			
			boolean isToPage = false;
			int toPage = 1;
			for (String key : ActionContextUtil.getParamMap().keySet()) {
				if (key.matches("d-[0-9]{7}-p")) {
					isToPage = true;
					toPage = Integer.parseInt(getRequestParameter(key));
					// displaytag 切換頁數中文被使用網址參數(轉換暫時無法解決,使用 seesion 處理)
					if (ActionContextUtil.getSessionAttr(sessionId) != null)
						bean.setInsured(String.valueOf(ActionContextUtil.getSessionAttr(sessionId)));
					break;
				}
			}
			ActionContextUtil.setSessionAttr(sessionId, bean.getInsured());
			executeWGRC04(toPage, isToPage,"query01");
			
			//下拉選擇:空白、全部、無單位
			if (type.equals("1")) {
				bean.setSubUnit4("");
			}else if (type.equals("2")) {
				bean.setSubUnit4("全部");
			}else if (type.equals("3")) {
					bean.setSubUnit4("無單位");
			}

			//如為自行輸入，要清空下拉
			if(StringUtils.isNotBlank(subUnitText4)){
				bean.setSubUnit("");
			}
			
			//顯示終止日
			String endDateShow = bean.getEndDateShow();
			
			ActionContextUtil.setRequestAttr("subUnitText4", subUnitText4); //分單位(輸入)
			ActionContextUtil.setRequestAttr("checkRedio", checkRedio); //要保單位顯示
			ActionContextUtil.setRequestAttr("endDateShow", endDateShow); //要保單位顯示
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			bean.setMessage(e.getMessage());
		}
		return SUCCESS;
	}
	
