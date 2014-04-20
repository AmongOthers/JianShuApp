package model;

import android.content.Context;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2014/4/12.
 */
public class UserInfo {

  private String userId;
  private String name;
  private String introduce;
  private String avatarUrl;

  public static UserInfo loadFromFileCache(Context context) {
    try {
      InputStreamReader reader = new InputStreamReader(context.openFileInput("userinfo"));
      BufferedReader bufferedReader = new BufferedReader(reader);
      String receiveString = null;
      StringBuilder stringBuilder = new StringBuilder();
      while ( (receiveString = bufferedReader.readLine()) != null ) {
        stringBuilder.append(receiveString);
      }
      Gson gson = new Gson();
      return gson.fromJson(stringBuilder.toString(), UserInfo.class);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static void save(Context context, UserInfo info) {
    Gson gson = new Gson();
    String json = gson.toJson(info);
    try {
      OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("userinfo", Context.MODE_PRIVATE));
      writer.write(json);
      writer.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static UserInfo load(Context context, String userId) {
    String userPageUrl = String.format("http://jianshu.io/users/%s/latest_articles", userId);
    Object httpResult = JianshuSession.getsInstance().getSync(userPageUrl, true);
    if(httpResult instanceof String) {
      UserInfo userInfo = new UserInfo();
      userInfo.userId = userId;
      Document doc = Jsoup.parse((String)httpResult);
      Element basicInfoEl = doc.select("div.basic-info").get(0);
      Element avatarEl = basicInfoEl.select("img").get(0);
      userInfo.avatarUrl = avatarEl.attr("src");
      Element nameEl = basicInfoEl.select("h3 a").get(0);
      userInfo.name = nameEl.text();
      Element introduceEle = basicInfoEl.select("p.intro").get(0);
      userInfo.introduce = introduceEle.text();
      save(context, userInfo);
      return userInfo;
    } else {
      return null;
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String mName) {
    this.name = mName;
  }

  public String getIntroduce() {
    return introduce;
  }

  public void setIntroduce(String mIntroduce) {
    this.introduce = mIntroduce;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String mAvatarUrl) {
    this.avatarUrl = mAvatarUrl;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
