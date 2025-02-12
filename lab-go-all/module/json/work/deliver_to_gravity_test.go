package work

import (
	jsoniter "github.com/json-iterator/go"
	"net/http"
	"net/url"
	"testing"
)

func Test_gravity(t *testing.T) {

	client := &http.Client{}

	params := url.Values{}
	params.Add("app_id", "29630815")
	params.Add("channel", "base_channel")
	params.Add("track_id", "f8zlDlbxsFvjIWN2")
	params.Add("company", "tencent")
	params.Add("advertiser_id", "9471147")
	params.Add("promoted_object_id", "wx69618ae091cf2c76")
	params.Add("aid", "228691429")
	params.Add("cid", "654321")
	params.Add("ts", "1586437362")
	params.Add("click_id", "24oi6xq2aaakvagnqu7a")
	params.Add("callback", "http://tracking.e.qq.com/conv?cb=xXx%2BxXx%3D&conv_id=123")
	params.Add("wx_openid", "ozWH25VK0aodxYMZrX0Lqj9HHhrg")
	params.Add("req_id", "vqp7xdombqonw")
	params.Add("csite", "SITE_SET_WECHAT")
	params.Add("ctc_info", "[{\"id\": 101234568,\"type\":\"image\"}]")
	params.Add("ele_info", "[{\"id\":\"123\"}]")
	params.Add("cid_name", "创意名称")
	params.Add("aid_name", "三国志·战略版iOS")
	params.Add("gid_name", "__CAMPAIGN_NAME__")

	finalURL := "https://backend.gravity-engine.com/event_center/api/v1/event/click/?" + params.Encode()

	t.Log(finalURL)

	resp, err := client.Get(finalURL)
	if err != nil {
		t.Log(err)
	} else {
		res, _ := jsoniter.MarshalToString(resp)
		t.Log(res)
	}

}
