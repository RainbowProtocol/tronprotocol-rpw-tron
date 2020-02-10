package org.tron.core.services.http;

import com.alibaba.fastjson.JSONObject;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;
import org.tron.protos.Protocol.MarketOrderList;


@Component
@Slf4j(topic = "API")
public class GetMarketOrderListByPairServer extends RateLimiterServlet {

  @Autowired
  private Wallet wallet;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    try {
      boolean visible = Util.getVisible(request);

      String sellTokenId = request.getParameter("sell_token_id");
      String buyTokenId = request.getParameter("buy_token_id");

      MarketOrderList reply = wallet.getMarketOrderListByPair(ByteArray.fromHexString(sellTokenId),
          ByteArray.fromHexString(buyTokenId));
      if (reply != null) {
        response.getWriter().println(JsonFormat.printToString(reply, visible));
      } else {
        response.getWriter().println("{}");
      }
    } catch (Exception e) {
      Util.processError(e, response);
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
      String input = request.getReader().lines()
          .collect(Collectors.joining(System.lineSeparator()));
      Util.checkBodySize(input);
      boolean visible = Util.getVisiblePost(input);
      JSONObject jsonObject = JSONObject.parseObject(input);

      String sellTokenId = jsonObject.getString("sell_token_id");
      String buyTokenId = jsonObject.getString("buy_token_id");

      MarketOrderList reply = wallet.getMarketOrderListByPair(ByteArray.fromHexString(sellTokenId),
          ByteArray.fromHexString(buyTokenId));
      if (reply != null) {
        response.getWriter().println(JsonFormat.printToString(reply, visible));
      } else {
        response.getWriter().println("{}");
      }
    } catch (Exception e) {
      Util.processError(e, response);
    }
  }
}
