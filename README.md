# IASとXSUAAの両方をバインドするCAP Java アプリケーションのサンプル

## 構成

- スタンドアロン App Router
- CAP アプリ
- IAS
- XSUAA
- 宛先サービス

1. /actuator/health/ping: ユーザからのアクセス向け。ブラウザ -> App Router -> IAS認証 -> CAP アプリ  
2. /actuator/health/db: システムからのアクセス向け。システム -> CAP アプリ  

2の方は  
`CAP アプリにバインドしている XSUAA のurl + /oauth/token` に XSUAA の `clientid` と `clientsecret` でBASIC認証で body に `grant_type=client_credentials` の POST リクエストを送信して（OAuth 2.0 クライアントクレデンシャルズフロー）、 `access_token` を取得し、CAP アプリのURLに `Authorization: Bearer <access_token>` ヘッダをつけてリクエストを送信することでアクセス可能。  

1、2ともCAPアプリにいきなり直接アクセスした場合は401エラーが返される。  
なお、2に対してApp RouterのURLにアクセスした場合は、ルーティングを設定していないため404エラーが返される。  

### BTP サブアカウントの設定

Security > Trust Configuration で `Establish Trust` ボタンを押し、IAS と OIDC で信頼関係を構築する。

### IAS の設定

#### Applications & Resources > Tenant Settings

General > Policy-Based Authorizations を有効化

#### Applications & Resources > Applications

##### cap01-ias アプリケーション

- Trust > Single Sign-On > OpenID Connect Configuration の Redirect URIs に `App Router の URL + /login/callback` が設定されていることを確認
- Trust > Single Sign-On > OpenID Connect Configuration の Advanced Settings の Access Token Format を `JSON Web Token` に設定
- Trust > Single Sign-On > Subject Name Identifier の Primary Attribute の Value を `Email` に設定
- Trust > Application APIs > Dependencies の Services に `cap01-ias (AMS)` と `SAP BTP subaccount サブアカウント名` が登録されている（※自分で設定したか自動的に設定されたかは失念）

※今回は作成していないが、CAP アプリのポリシーを設定している場合はデプロイすると Authorization Policies に表示されるので、それをユーザに割り当てることでアプリの権限管理が可能。

## 注意

- このサンプルで動作を確認したのは、上記のパスによる認証の挙動のみ
- とりあえず、動くようになったというだけで不要な設定はおそらく色々あると思われる
- DBはHANA Cloudの設定となっているが、認証の確認には不要のため未利用。動かしているとDB接続エラーは出るが無視している

## 参考 URL

- Enabling Authorization Policies in SAP Cloud Identity Services - IAS with AMS/CAP https://community.sap.com/t5/technology-blog-posts-by-sap/enabling-authorization-policies-in-sap-cloud-identity-services-ias-with-ams/ba-p/13972718
- Authorization Management Service (AMS)を利用したCAPの権限管理 - アップデート https://qiita.com/tami/items/2c3696fcf5718f0c9515
- Configure Authorization and Authentication Using the Authorization Management Service and the Identity Authentication Service https://github.com/SAP-samples/btp-developer-guide-cap/blob/main/documentation/xsuaa-to-ams/README.md
- SAP BTP の Identity Service の参照情報 https://help.sap.com/docs/cloud-identity-services/cloud-identity-services/reference-information-for-identity-service-of-sap-btp?locale=ja-JP
- SAP Authorization and Trust Management サービス と SAP Cloud Identity Services の間のトラストおよび連携の確立 https://help.sap.com/docs/authorization-and-trust-management-service/authorization-and-trust-management/establish-trust-and-federation-between-uaa-and-identity-authentication
- routes https://help.sap.com/docs/cloud-portal-service/sap-cloud-portal-service-on-cloud-foundry/routes?locale=ja-JP
- @sap/approuter https://www.npmjs.com/package/@sap/approuter

