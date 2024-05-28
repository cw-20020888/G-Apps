JSV.addLang('sys',{
	apStatus:{
		ING:'進行中',
		END:'決済完了',
		CANCEL:'上申キャンセル',
		RETURN:'回収',
		REJECT:'却下',
		TEMP:'一時保存',
		HOLD:'保留',
		ARRIVE:'到着',
		LINK_CANCEL:'접수취소'
	},
	apStep:{
		AP:'決裁',
		CP:'合意',
		AD : '감사',
		DCP:'協力',
		RCV:'受付',
		PREVAP:'専決',
		LINK_CANCEL:'취소',
		DC:'전결'
	},
	lineStatus:{
		READY:'待機',
		ING:'進行中',
		SIGN:'承認',
		HOLD:'保留',
		REJECT:'却下',
		LINK_CANCEL:'접수취소'
	},
	acctYn:{
		Y:'受付',
		N:'未受付'
	},
	scrtType:{
		ALL:'公開',
		DEPT:'部署公開',
		PART:'部分公開',
		CLOSED:'非公開'
	},
});
JSV.addLang('nav',{
	undecided:'未決箱',
	decided:'既決箱',
	finished:'個人完了箱',
	enterprise:'全社文書箱',
	dprtDoc:'部署文書箱',
	preview:'プレビュー',
	shared:'供覧箱',
	referenced:'参照箱',
	received:'受信箱',
	sended:'発送箱',
	adminDoc:'決裁文書管理',
	draft:'起案箱',
	dprtAdminDoc:'문서함 관리',
	transfer : '문서함분류 관리',
	exprDoc:'삭제된 문서함 관리',
});
JSV.addLang('btn',{ 
	'001':'追加',
	'002':'削除',
	'003':'保存',
	'004':'決裁',
	'005':'全体',
	'006':'1月',
	'007':'2月',
	'008':'1年',
	'009':'2年',
	'010':'移動',
	'011':'キャンセル' ,
	'012':'専決',
	'013':'合意',
	'014':'受付',
	'015':'却下',
	'016':'修正',
	'017':'決裁者',
	'018':'合意者',
	'019':'受付者',
	'020':'選択',
	'021':'除外',
	'022':'上へ',
	'023':'下へ',
	'024':'現在の決裁ラインで個人決裁ラインを作る',
	'025':'確認',
	'026':'決裁情報',
	'027':'書き込み',
	'028':'이관',
	'029':'등록',
	'030':'감사자',
	'031':'접수취소',
	'032' : '가',
	'033' : '나',
	'034' : '다',
	'035' : '라',
	'036' : '마',
	'037' : '바',
	'038' : '사',
	'039' : '아',
	'040' : '자',
	'041' : '카',
	'042' : '타',
	'043' : '파',
	'044' : '하',
});
JSV.addLang('doc',{
	'exprMonth' : '6:6ヶ月,12:1年,24:2年,60:5年,120:10年,240:20年,-1:永久',	
	'001':'ファイル名',
	'002':'ファイルサイズ',
	'003':'ファイル個数',
	'004':'件',
	'005':'イメージ選択',
	'006':'[代決]',
	'007':'リストに出力する結果がありません。',
	'008':'受信履歴',
	'009':'タイトル',
	'010':'起案者',
	'011':'文書番号',
	'012':'入力キャンセル',
	'013':'検索',
	'014':'詳細検索',
	'015':'開く',
	'016':'閉じる',
	'017':'検索オプションの初期化',
	'018':'選択したドキュメントを起案箱から',
	'019':'決裁',
	'020':'フォーム名',
	'021':'全体',
	'022':'起案日時',
	'023':'日程',
	'024':'1年',
	'025':'2年',
	'026':'決裁状態',
	'027':'総',
	'028':'件',
	'029':'昇順で並べ替え',
	'030':'降順で並べ替え',
	'031':'代決',
	'032':'修正',
	'033':'決裁パスワード',
	'034':'修正理由',
	'035':'意見',
	'036':'文字数',
	'037':'決裁ラインの指定',
	'038':'個人決裁ラインの指定',
	'039':'宛先指定',
	'040':'受付指定',
	'041':'ユーザーの追加',
	'042':'組織図',
	'043':'個人決裁ライン',
	'044':'ユーザー',
	'045':'初期化',
	'046':'名前',
	'047':'所属',
	'048':'職級',
	'049':'決裁ライン',
	'050':'宛先',
	'051':'ステップ',
	'052':'情報',
	'053':'基本',
	'054':'担当者',
	'055':'個人',
	'056':'部門',
	'057':'起案',
	'058':'関連ドキュメントの選択',
	'059':'人',
	'060':'決裁完了日',
	'061':'※ご希望の資料をチェックし,追加ボタンをクリックしてください。',
	'062':'担当者設定',
	'063':'代決者の指定',
	'064':'担当',
	'065':'代決',
	'066':'フォームエディタ',
	'067':'基本情報',
	'068':'文書番号フォーマット',
	'069':'文書番号の長さ',
	'070':'複数選択',
	'071':'決裁タイトル',
	'072':'決裁欄の数',
	'073':'最大の長さ',
	'074':'高さ(px)',
	'075':'幅(px)',
	'076':'デフォルト',
	'077':'読み専用',
	'078':'必須チェック',
	'079':'単数',
	'080':'複数',
	'081':'年月日',
	'082':'年:月:日',
	'083':'年。月。日',
	'084':'年/月/日',
	'085':'年 - 月 - 日',
	'086':'すべて',
	'087':'数字',
	'088':'登録',
	'089':'SQLの編集ウィンドウ',
	'090':'全体公開',
	'091':'部門公開',
	'092':'部分公開',
	'093':'プライベート',
	'094':'セキュリティ設定',
	'095':'コンポーネントのプロパティ',
	'096':'追加/削除',
	'097':'6ヶ月',
	'098':'5年',
	'099':'10年',
	'100':'20年',
	'101':'永久', 
	'102':'登録日',
	'103':'作成者',
	'104':'使用',
	'105':'使用しない',
	'106':'決裁フォーム箱',
	'107':'共用フォーム箱',
	'108':'電子決裁文書を修正,却下管理します。',
	'109':'添言',
	'110':'選択した文書を',
	'111':'メール受信組織選択',
	'112':'組織リスト',
	'113':'組織名',
	'114':'組織ID',
	'115':'言語',
	'116':'加入日',
	'117':'意見入力',
	'118':'決裁ラインの変更',
	'119':'外部連動キー',
	'120':'協力先',
	'121':'番号',
	'122':'表示する分類箱が存在しません。',
	'123':'伝票番号',
	'124':'대장관리',
	'125':'협조',
	'126':'결재안함',
	'127':'배포처지정',
	'128':'접수번호',
	'129':'발신기관',
	'130':'발송번호',
	'131':'전체 문서를',
	'132':'기본분류함',
	'133':'선택해주세요.',
	'134':'이메일',
	'135':'아이피',
	'136':'첨부된 파일',
	'137':'사번'
});
JSV.addLang('msg',{
	'001':'期間を再確認してください。',
	'002':'移動できないページ番号です。\ n再入力してください。',
	'003':'許可されたデータタイプはありません。',
	'004':'有効なデータ型がありません。',
	'005':'文書番号の長さを入力してください。',
	'006':'文書番号フォーマットを入力してください。',
	'007':'を入力してください。',
	'008':'は,(は)数字のみを入力してください。',
	'009':'保存されて',
	'010':'アイテムは,数字のみ入力可能です。' ,
	'011':'FORMATを指定してください。',
	'012':'[日付]必須入力値です。入力してください。',
	'013':'[ドキュメントのタイトル]必須入力値です。入力してください',
	'014':'決裁パスワードを入力してください。',
	'015':'決裁パスワードを再入力してください。',
	'016':'意見を登録してください。',
	'017':'ユーザー名を入力してください。' ,
	'018':'決裁者を選択してください。',
	'019':'合意者を選択してください。',
	'020':'専決者を選択してください。',
	'021':'受付者を選択してください。',
	'022':'様は,すでに追加されました。',
	'023':'受付者は一人のみとなります。',
	'024':'個人決裁ラインを選択してください。',
	'025':'決裁ラインは初期化され設定されます。\ n続行しますか？',
	'026':'受付者が指定された場合,個人決裁ラインを保存することができません。',
	'027':'決裁ラインを選択してください。',
	'028':'移動決裁者を選択してください。',
	'029':'決裁ラインを設定してください。',
	'030':'最後の決裁ラインは,[受付者]に設定する必要があります。',
	'031':'最後の決裁ラインは[決裁者],[受付者]の順に設定する必要があります。',
	'032':'[決裁者]を設定してください。',
	'033':'[受付者]以前の決裁ラインは,[決裁者]に設定する必要があります。',
	'034':'最後の決裁ラインは,[決裁者]に設定する必要があります。',
	'035':'キーワードを入力してください。',
	'036':'文書と同じ文書が追加されています。',
	'037':'追加する関連文書を選択してください。',
	'038':'削除することが関連文書を選択してください。',
	'039':'関連文書を追加してください。',
	'040':'ユーザーまたは部署を設定してください。',
	'041':'ユーザーや部門を選択してください。',
	'042':'は,追加されました。',
//	'043':'담당자를 설정해주세요.',
//	'044':'代決者を設定してください。',
	'045':'制限文字数を超えています。',
	'046':'セキュリティ設定部門またはユーザーを選択してください。',
	'047':'必須入力値です。',
	'048':'の有効な電子メールの形式がありません。',
	'049':'有効なURLの形式がありません。',
	'050':'Googleのメールを入力してください。',
	'051':'メールを送る組織を追加してください。',
	'052':'許可された添付ファイルの容量を超えました。',
	'053':'無効なファイルの種類です。',
	'054':'有効な番号の形式がありません。',
	'055':'削除しますか。',
	'056':'許可されたすべてのファイルの容量を超えました。',
	'057':'許可されたファイル数が超過しました。',
	'058':'受付者前決裁ラインは[決裁者]に設定する必要があります。',
	'059':'受付者は一人のみとなります。',
	'060':'回答が入力されてい保存しますか。',
	'061':'意見が修正されました保存しますか。' ,
	'062':'変更されたコンポーネントの値が保存されました。',
	'063':'削除する組織を選択してください。',
	'064':'既に追加された協力先です。',
	'065':'決裁者は',
	'066':'合議者は',
	'067':'受付者は',
	'068':'命まで可能です。',
	'069':'受付者以外の決裁先だけ保存されます。',
	'070':'重複した名前です。',
	'071':'名前にコンマは入力できません。',
	'072':'必衰名前を入力してください。',
	'073':'합의, 협조를 제외한 결재선만 셋팅되고,\n',
	'074':'決裁者は個人だけ指定可能です。',
	'075':'受付者は個人だけ指定可能です。',
	'076':'감사자를 선택해주세요.',
	'077':'마지막 결재자 이전엔 감사가 들어가야 합니다.',
	'078':'감사자는 ',
	'079':'협조는 ',
	'080':'감사자를 제외한 결재선만 셋팅되고,\n',
//	'081':'협조처를 제외한 결재선만 셋팅되고,\n',
	'082':'이미지파일을 업로드 해주세요.',
	'083':'감사자가 여러명인경우 연속적인 순서를 가져야 합니다.',
	'084':'[분류함] 분류내용을 선택해주세요.',
	'085':'선택해주세요.',
	'086':'현재 결재자가 존재하지 않습니다.\n다시 시도하여 주세요.',
	'087':'접수자가 여러명인경우 연속적인 순서를 가져야 합니다.',
	'088':'[분류코드] 필수입력값입니다.',
	'089' : '[회사로고]가 존재하지 않습니다.',
	'090' : '[회사인장]이 존재하지 않습니다.',
	'091' : '필수입력값입니다. 입력해주세요.',
	'092' : '유효한 IP형식이 아닙니다.',
	'093' : '기본분류함을 설정해주세요.',
});
JSV.addLang('linkmsg',{ 
	'001':'借方項目を追加してください。',
	'002':'貸方項目を追加してください。',
	'003':'借方と貸方の値が正しくありません。\ n確認ください。',
});