package io.retailplanet.backend.common.processor;

import java.lang.annotation.*;

/**
 * Annotation that defines the URL for a retailplanet service
 *
 * @author w.glanzer, 05.09.2019
 */
@Target({ElementType.TYPE})
public @interface URL
{

  /**
   * @return Name of the target module, where the rest service is located
   */
  String targetModule();

  /**
   * @return BaseURL which will be added to all modules
   */
  String baseURL() default "${SERVICES_BASEURL}";

}
