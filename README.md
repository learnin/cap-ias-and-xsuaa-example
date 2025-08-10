# IASとXSUAAの両方をバインドするCAP Java アプリケーションのサンプル

## 構成

- スタンドアロン App Router
- CAP アプリ
- IAS
- XSUAA
- 宛先サービス
- HANA Cloud

1. /actuator/health/ping, /rest/, /odata/: ユーザからのアクセス向け。ブラウザ -> App Router -> IAS認証 -> CAP アプリ  
2. /actuator/health/db: 他システムからのアクセス向け。他システム -> CAP アプリ  

2の方は  
`CAP アプリにバインドしている XSUAA のurl + /oauth/token` に XSUAA の `clientid` と `clientsecret` でBASIC認証で body に `grant_type=client_credentials` の POST リクエストを送信して（OAuth 2.0 クライアントクレデンシャルズフロー）、 `access_token` を取得し、CAP アプリのURLに `Authorization: Bearer <access_token>` ヘッダをつけてリクエストを送信することでアクセス可能。  

1、2ともCAPアプリにいきなり直接アクセスした場合は401エラーが返される。  
なお、2に対してApp RouterのURLにアクセスした場合は、ルーティングを設定していないため404エラーが返される。  

### BTP サブアカウントの設定

Security > Trust Configuration で `Establish Trust` ボタンを押し、IAS と OIDC で信頼関係を構築する。

### Build & Deploy

```
mta build
cf deploy mta_archives/cap01_1.0.0-SNAPSHOT.mtar
```

ローカルで動かすときは `mvn spring-boot:run` など。  

### IAS の設定

#### Applications & Resources > Tenant Settings

General > Policy-Based Authorizations を有効化

#### Applications & Resources > Applications

##### cap01-ias アプリケーション

- Trust > Single Sign-On > OpenID Connect Configuration の Redirect URIs に `App Router の URL + /login/callback` が設定されていることを確認
- Trust > Single Sign-On > OpenID Connect Configuration の Advanced Settings の Access Token Format を `JSON Web Token` に設定
- Trust > Single Sign-On > Subject Name Identifier の Primary Attribute の Value を `Email` に設定
- Trust > Application APIs > Dependencies の Services に `cap01-ias (AMS)` と `SAP BTP subaccount サブアカウント名` が登録されている
- アプリをデプロイすると、Authorization Policies に CDS で設定しているロールがポリシーとして登録されるので、それをユーザに割り当てることでアプリの権限管理を行う

## 注意

- とりあえず、動くようになったというだけで不要な設定はおそらく色々あると思われる
- ローカル（BAS）で動かす場合、IASがバインドされないため、`cloud` 以外のプロファイルで起動することで、BASIC 認証でモックユーザでログインすることになるが、現状、ログインすると次のエラーが発生する。
  ```
  com.sap.cds.services.utils.ErrorStatusException: Internal server error
        at com.sap.cloud.security.ams.capsupport.AmsUserInfoProvider.get(AmsUserInfoProvider.java:152) ~[cap-ams-support-2.5.0.jar:na]
        at com.sap.cds.services.impl.runtime.CdsRuntimeImpl.getProvidedUserInfo(CdsRuntimeImpl.java:116) ~[cds-services-impl-4.0.2.jar:na]
        at com.sap.cds.services.impl.runtime.RequestContextRunnerImpl.providedUserInfo(RequestContextRunnerImpl.java:308) ~[cds-services-impl-4.0.2.jar:na]
        ...
  ```
  回避するには一旦ローカルではAMS設定を外す。具体的には `srv/pom.xml` の
  ```
  		<dependency>
			<groupId>com.sap.cloud.security.ams.client</groupId>
			<artifactId>jakarta-ams</artifactId>
			<version>${sap.cloud.security.ams.version}</version>
		</dependency>

		<dependency>
			<groupId>com.sap.cloud.security.ams.client</groupId>
			<artifactId>cap-ams-support</artifactId>
			<version>${sap.cloud.security.ams.version}</version>
		</dependency>
  ```
  と
  ```
  								<command>build --for ams</command>
  ```
  をコメントアウトし、 `package.json` の
  ```
      "@sap/ams" : "^3"
  ```
  を削除する。

## 参考 URL

- Enabling Authorization Policies in SAP Cloud Identity Services - IAS with AMS/CAP https://community.sap.com/t5/technology-blog-posts-by-sap/enabling-authorization-policies-in-sap-cloud-identity-services-ias-with-ams/ba-p/13972718
- Authorization Management Service (AMS)を利用したCAPの権限管理 - アップデート https://qiita.com/tami/items/2c3696fcf5718f0c9515
- Configure Authorization and Authentication Using the Authorization Management Service and the Identity Authentication Service https://github.com/SAP-samples/btp-developer-guide-cap/blob/main/documentation/xsuaa-to-ams/README.md
- SAP BTP の Identity Service の参照情報 https://help.sap.com/docs/cloud-identity-services/cloud-identity-services/reference-information-for-identity-service-of-sap-btp?locale=ja-JP
- SAP Authorization and Trust Management サービス と SAP Cloud Identity Services の間のトラストおよび連携の確立 https://help.sap.com/docs/authorization-and-trust-management-service/authorization-and-trust-management/establish-trust-and-federation-between-uaa-and-identity-authentication
- routes https://help.sap.com/docs/cloud-portal-service/sap-cloud-portal-service-on-cloud-foundry/routes?locale=ja-JP
- @sap/approuter https://www.npmjs.com/package/@sap/approuter

