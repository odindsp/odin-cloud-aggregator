package com.pxene.odin.cloud.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pxene.odin.cloud.domain.BaseArgumentValidation;
import com.pxene.odin.cloud.domain.BaseResponse;
import com.pxene.odin.cloud.exception.BaseException;
import com.pxene.odin.cloud.exception.IllegalArgumentException;
import com.pxene.odin.cloud.exception.ServerFailureException;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理类：针对所有Controller抛出的异常进行最后的处理。
 * <ul>
 *  <li>如果抛出的是自定义异常，则使用自定义异常类中的属性，包装一个通用错误对象（包含一个整型的code和一个字符串类型的msg）。</li>
 *  <li>如果抛出的是除自定义异常以外的异常，则统一使用HttpStatus Code 500作为响应码。</li>
 * </ul>
 * @author ningyu
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandlerConfig
{
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public BaseResponse handleException(Exception exception, HttpServletResponse response)
    {
        if (exception instanceof BaseException)
        {
            BaseException baseException = (BaseException) exception;
            log.debug(baseException.toString());

            int httpStatusCode = baseException.getHttpStatus().value();
            int bizStatusCode = baseException.getCode();
            String bizStatusMsg = baseException.getMessage();

            response.setStatus(httpStatusCode);
            return new BaseResponse(bizStatusCode, bizStatusMsg);
        }
        else
        {
            if (MethodArgumentNotValidException.class.isInstance(exception))
            {
                log.warn("Attention there are some warning, see details: ", exception);
                List<BaseArgumentValidation> invalidArguments = getBindResultErrors(exception);

                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return new BaseResponse(IllegalArgumentException.ERROR_CODE, IllegalArgumentException.ERROR_MSG, invalidArguments);
            }
            else
            {
                log.error("Some error occured, see details:", exception);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return new BaseResponse(ServerFailureException.ERROR_CODE, ServerFailureException.ERROR_MSG);
            }
        }
    }

    /**
     * 从原始的MethodArgumentNotValidException异常中解析出原始错误信息，按需重新封装后返回。
     * @param exception     原始异常对象
     * @return              返回非法的字段名称、原始值、错误信息
     */
    private List<BaseArgumentValidation> getBindResultErrors(Exception exception)
    {
        List<BaseArgumentValidation> invalidArguments = new ArrayList<BaseArgumentValidation>();

        MethodArgumentNotValidException notValidException = (MethodArgumentNotValidException) exception;
        BindingResult bindingResult = notValidException.getBindingResult();

        for (FieldError error : bindingResult.getFieldErrors())
        {
            BaseArgumentValidation invalidArgument = new BaseArgumentValidation(error.getField(), error.getRejectedValue(), error.getDefaultMessage());
            invalidArguments.add(invalidArgument);
        }

        return invalidArguments;
    }
}
