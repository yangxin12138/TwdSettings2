package com.twd.setting.module.network.speed;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.base.BaseFragment;
import com.twd.setting.databinding.FragmentNetworkSpeedBinding;
import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkDataUtils;
import com.twd.setting.utils.StringUtil;
import com.twd.setting.utils.TimeUtils;
import com.twd.setting.utils.manager.DataMapUtils;
import com.twd.setting.widgets.CustomDialView;
import com.twd.setting.widgets.ToastTools;
//import org.apache.http.util.TextUtils;

public class NetworkSpeedFragment
        extends BaseFragment {
    private static final String ARG_DOWNLOAD_URL = "ARG_DOWNLOAD_URL";
    private static final String LOG_TAG = "NetworkSpeedFragment";
    private String aveSpeed;
    private FragmentNetworkSpeedBinding binding;
    private ConnectivityManager connMgr;
    private String downloadUrl;
    private String ipAddress;
    private String netType;
    private NetworkSpeedTester speedTester;
    private NetworkSpeedViewModel viewModel;

    public static NetworkSpeedFragment newInstance(String paramString) {
        NetworkSpeedFragment fragment = new NetworkSpeedFragment();
        Bundle localBundle = new Bundle();
        localBundle.putString("ARG_DOWNLOAD_URL", paramString);
        fragment.setArguments(localBundle);
        return fragment;
    }

    private void startTestSpeed() {
        if (Build.VERSION.SDK_INT < 21) {
            ToastTools.Instance().showToast(requireContext(), "安卓版本低于5.0");
            return;
        }
        int i = this.viewModel.getNetType(this.connMgr.getActiveNetworkInfo());
        boolean bool = true;
        if (i == 1) {
            netType = "无线网络";
        } else if (i == 2) {
            netType = "有线网络";
        } else {
            netType = "";
        }

        Object localObject3 = this.connMgr.getActiveNetworkInfo();
        Object localObject2 = null;

        if (localObject3 == null) {
            Log.d(LOG_TAG, "activeNetworkInfo = null");
        } else {
            Log.d(LOG_TAG, "activeNetworkInfo.isConnected: " + ((NetworkInfo) localObject3).isConnected());
        }

        Object localObject1 = localObject2;
        if (localObject3 != null) {
            localObject1 = localObject2;
            if (((NetworkInfo) localObject3).isConnected()) {
                localObject1 = new StringBuilder();
                ((StringBuilder) localObject1).append("Build.VERSION.SDK_INT >= Build.VERSION_CODES.M： ");
                int j = Build.VERSION.SDK_INT;
                i = 0;
                if (j < 23) {
                    bool = false;
                }
                ((StringBuilder) localObject1).append(bool);
                Log.d(LOG_TAG, ((StringBuilder) localObject1).toString());
                if (Build.VERSION.SDK_INT >= 23) {
                    localObject1 = this.connMgr.getActiveNetwork();
                } else {
                    j = connMgr.getAllNetworks().length;
                    for (; ; ) {
                        localObject1 = localObject2;
                        if (i >= j) {
                            break;
                        }

                        NetworkInfo localNetworkInfo = this.connMgr.getNetworkInfo(i);
                        if (localNetworkInfo == null) {
                            localObject1 = "networkInfo = null";
                        } else {
                            localObject1 = new StringBuilder();
                            ((StringBuilder) localObject1).append("networkInfo.isConnected：");
                            ((StringBuilder) localObject1).append(localNetworkInfo.isConnected());
                            localObject1 = ((StringBuilder) localObject1).toString();
                        }
                        Log.d(LOG_TAG, (String) localObject1);
                        if ((localNetworkInfo != null) && (localNetworkInfo.isConnected())) {
                            Log.d(LOG_TAG, "--> It has active network");

                            break;
                        }
                        i += 1;
                    }
                }
            }
        }
        if (localObject1 == null) {
            ToastTools.Instance().showToast(requireContext(), "请检查网络状况");
            return;
        }
        ipAddress = viewModel.getIpAddress((Network) localObject1);
        speedTester.startTestSpeed();
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        if (getArguments() != null) {
            downloadUrl = getArguments().getString("ARG_DOWNLOAD_URL", "");
        }

        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("download url: ");
        localStringBuilder.append(downloadUrl);
        Log.d(LOG_TAG, localStringBuilder.toString());
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        binding = (FragmentNetworkSpeedBinding) DataBindingUtil.inflate(paramLayoutInflater, R.layout.fragment_network_speed, paramViewGroup, false);
        return binding.getRoot();
    }

    public void onDestroy() {
    //    speedTester.setShouldSendHandlerMsg(false);
        super.onDestroy();
    }

    public void onResume() {
   //     speedTester.setShouldSendHandlerMsg(true);
        super.onResume();
    }

    public void onViewCreated(View view, Bundle paramBundle) {
        super.onViewCreated(view, paramBundle);
        initTitle(view, R.string.fragment_title_net_speed_test);
        if (TextUtils.isEmpty(this.downloadUrl)) {
            ToastTools.Instance().showToast(requireContext(), "获取测速地址失败");
            return;
        }
        connMgr = ((ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        viewModel = (NetworkSpeedViewModel) new ViewModelProvider(requireActivity()).get(NetworkSpeedViewModel.class);

        speedTester = viewModel.newHandler(this.downloadUrl, new NetworkSpeedTester.INetSpeedTest() {
            public void onEnd() {
                binding.tvSpeedMeasureProcess.setText("测速进度：100%");
            }

            public void onResult(String paramAnonymousString) {
//        NetworkSpeedFragment.access$202(NetworkSpeedFragment.this, paramAnonymousString);
                binding.containerTesting.setVisibility(View.GONE);
                binding.containerTestFinished.setVisibility(View.VISIBLE);
                binding.btnRetestSpeed.requestFocus();
                binding.tvNetworkFluency.setText("网络流畅度："+viewModel.getNetFluency(paramAnonymousString));
                binding.tvNetworkType.setText("当前网络："+netType);
                binding.tvAverageSpeed.setText("平均速度：");
                binding.tvIpAddress.setText("IP地址："+ipAddress);
                /*if (!TextUtils.isEmpty(aveSpeed)) {
                    paramAnonymousString = TimeUtils.getNowString();
                    localObject1 = new StringBuilder();
                    ((StringBuilder) localObject1).append("{\"当前网络\":");
                    ((StringBuilder) localObject1).append(NetworkSpeedFragment.this.netType);
                    ((StringBuilder) localObject1).append(", \"平均速度\":");
                    ((StringBuilder) localObject1).append(NetworkSpeedFragment.this.aveSpeed);
                    ((StringBuilder) localObject1).append("}");
                    localObject1 = ((StringBuilder) localObject1).toString();
                    localObject2 = NetworkSpeedFragment.LOG_TAG;
                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append("埋点上报 功能调用: --> time: ");
                    localStringBuilder.append(paramAnonymousString);
                    localStringBuilder.append(", function_name：");
                    localStringBuilder.append("网络测速");
                    localStringBuilder.append(", function_value: ");
                    localStringBuilder.append((String) localObject1);
                    Log.d(LOG_TAG, localStringBuilder.toString());
                    //        KkDataUtils.sent("systemsetting10_function_active", DataMapUtils.getInstance("time", paramAnonymousString).putData("function_name", "网络测速").putData("function_value", (String)localObject1).getDataMap());
                }

                 */
            }

            public void onStart() {
                binding.containerTesting.setVisibility(View.VISIBLE);
                binding.containerTestFinished.setVisibility(View.GONE);
                binding.tvSpeedMeasureProcess.setText("测速进度：0%");
                binding.tvCurrentSpeed.setText("当前速度：0B/s");
                binding.tvAverageSpeedTesting.setText("平均速度：0B/s");
            }

            public void onTesting(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3) {
 /*       Object localObject = NetworkSpeedFragment.this.binding.tvSpeedMeasureProcess;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("测速进度：");
        localStringBuilder.append(paramAnonymousString1);
        binding.tvSpeedMeasureProcess.setText(localStringBuilder.toString());
        StringBuilder localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("当前速度：");
        ((StringBuilder)localObject2).append(binding.tvCurrentSpeed);
        paramAnonymousString1.setText(((StringBuilder)localObject2).toString());
        paramAnonymousString1 = NetworkSpeedFragment.this.binding.tvAverageSpeedTesting;
        paramAnonymousString2 = new StringBuilder();
        paramAnonymousString2.append("平均速度：");
        paramAnonymousString2.append(paramAnonymousString3);
        paramAnonymousString1.setText(paramAnonymousString2.toString());
        paramAnonymousString1 = StringUtil.matchFloat(paramAnonymousString3);
        float f = Float.parseFloat(paramAnonymousString1);
        paramAnonymousString2 = NetworkSpeedFragment.LOG_TAG;
        StringBuilder localObject = new StringBuilder();
        ((StringBuilder)localObject).append("str av: ");
        ((StringBuilder)localObject).append(paramAnonymousString3);
        ((StringBuilder)localObject).append(", num ave: ");
        ((StringBuilder)localObject).append(paramAnonymousString1);
        HLog.d(LOG_TAG, ((StringBuilder)localObject).toString());
        NetworkSpeedFragment.this.binding.cdv.start(f);

  */
            }
        });
//    this.binding.btnRetestSpeed.setOnClickListener(new NetworkSpeedFragment..ExternalSyntheticLambda0(this));
        startTestSpeed();
    }
}
