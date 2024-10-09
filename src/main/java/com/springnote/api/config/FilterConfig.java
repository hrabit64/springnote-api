package com.springnote.api.config;

import com.springnote.api.filter.RequestResponseLoggingFilter;
import com.springnote.api.filter.SetupRequestContextFilter;
import com.springnote.api.filter.reReadableRequest.ReReadableRequestFilter;
import com.springnote.api.utils.context.RequestContext;
import com.springnote.api.utils.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@RequiredArgsConstructor
@Configuration
public class FilterConfig {

    //    private final GeneralRateLimitConfig generalRateLimitConfig;
    private final CommentConfig commentConfig;

    @Bean
    public ReReadableRequestFilter reReadableRequestFilter() {
        return new ReReadableRequestFilter();
    }

//    @DependsOn("generalRateLimitConfig")
//    @Bean
//    public RateLimitFilter generalRateLimitFilter() {
//        return new RateLimitFilter(
//                generalRateLimitConfig.getRateLimitBuketName(),
//                generalRateLimitConfig.getRateLimitResetTime(),
//                generalRateLimitConfig.getRateLimitMaxCount(),
//                generalRateLimitConfig.getRateLimitDisadvantageWrite(),
//                generalRateLimitConfig.getRateLimitDisadvantageView()
//        );
//    }

//    @DependsOn("commentConfig")
//    @Bean
//    public RateLimitFilter commentRateLimitFilter() {
//        return new RateLimitFilter(commentConfig.getRateLimitBuketName(), commentConfig.getRateLimitResetTime(), commentConfig.getRateLimitMaxCount());
//    }

    @DependsOn({"requestContext", "jsonUtil"})
    @Bean
    public RequestResponseLoggingFilter requestResponseLoggingFilter(RequestContext requestContext, JsonUtil jsonUtil) {
        return new RequestResponseLoggingFilter(requestContext, jsonUtil);
    }

    @DependsOn("userContext")
    @Bean
    public SetupRequestContextFilter setupRequestContextFilter(RequestContext requestContext) {
        return new SetupRequestContextFilter(requestContext);
    }

    @DependsOn("reReadableRequestFilter")
    @Bean
    public FilterRegistrationBean<ReReadableRequestFilter> reReadableRequestFilterRegistration(ReReadableRequestFilter reReadableRequestFilter) {
        var filterRegistrationBean = new FilterRegistrationBean<>(reReadableRequestFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("ReReadableRequestFilter");
        filterRegistrationBean.setOrder(0); // 필터 실행 순서

        return filterRegistrationBean;
    }
//
//    @DependsOn("generalRateLimitFilter")
//    @Bean
//    public FilterRegistrationBean<RateLimitFilter> generalRateLimitFilterRegistration(RateLimitFilter generalRateLimitFilter) {
//        var filterRegistrationBean = new FilterRegistrationBean<>(generalRateLimitFilter);
//        filterRegistrationBean.addUrlPatterns("/*");
//        filterRegistrationBean.setName("GeneralRateLimitFilter");
//        filterRegistrationBean.setOrder(2); // 필터 실행 순서
//
//        return filterRegistrationBean;
//    }
//

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        var filterRegistrationBean = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("ETagHeaderFilter");
        filterRegistrationBean.setOrder(3); // 필터 실행 순서

        return filterRegistrationBean;
    }


    @DependsOn("setupRequestContextFilter")
    @Bean
    public FilterRegistrationBean<SetupRequestContextFilter> setupRequestContextFilterRegistration(SetupRequestContextFilter setupRequestContextFilter) {
        var filterRegistrationBean = new FilterRegistrationBean<>(setupRequestContextFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("SetupRequestContextFilter");
        filterRegistrationBean.setOrder(4); // 필터 실행 순서

        return filterRegistrationBean;
    }

    @DependsOn("requestResponseLoggingFilter")
    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> requestResponseLoggingFilterRegistration(RequestResponseLoggingFilter requestResponseLoggingFilter) {
        var filterRegistrationBean = new FilterRegistrationBean<>(requestResponseLoggingFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("RequestResponseLoggingFilter");
        filterRegistrationBean.setOrder(5); // 필터 실행 순서

        return filterRegistrationBean;
    }


}
