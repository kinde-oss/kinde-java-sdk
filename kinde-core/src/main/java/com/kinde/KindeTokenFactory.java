package com.kinde;

import com.kinde.token.KindeToken;

public interface KindeTokenFactory {


    KindeToken parse(String token);

}
