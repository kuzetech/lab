package runtime

import (
	"github.com/shirou/gopsutil/v3/cpu"
	"github.com/shirou/gopsutil/v3/host"
	"github.com/shirou/gopsutil/v3/mem"
	"github.com/shirou/gopsutil/v3/net"
	"testing"
)

func Test_host(t *testing.T) {
	info, _ := host.Info()
	t.Log(info.Hostname)
}

func Test_memory(t *testing.T) {
	info, _ := mem.VirtualMemory()
	t.Logf("MemoryTotal: %v, MemoryUsedPercent: %f%% \n", info.Total, info.UsedPercent)
}

func Test_cpu(t *testing.T) {
	infoArray, _ := cpu.Percent(0, false)
	for _, info := range infoArray {
		t.Logf("CPUUsedPercent: %f%% \n", info)
	}
}

func Test_net(t *testing.T) {
	infoArray, _ := net.IOCounters(false)
	for _, info := range infoArray {
		t.Logf("BytesRecv: %v, BytesSent: %v \n", info.BytesRecv, info.BytesSent)
	}
}
