/*
 * Copyright (c) 2018, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http:www.apache.orglicensesLICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.redis.actions.zset;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.redis.Constants;
import org.ballerinalang.redis.RedisDataSource;
import org.ballerinalang.redis.RedisDataSourceUtils;
import org.ballerinalang.redis.actions.AbstractRedisAction;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code {@link ZAdd}} Maps with "ZADD" operation of Redis.
 *
 * @since 0.5.0
 */
@BallerinaFunction(orgName = "ballerina",
                   packageName = "redis",
                   functionName = "zAdd",
                   receiver = @Receiver(type = TypeKind.STRUCT,
                                        structType = Constants.REDIS_CLIENT))
public class ZAdd extends AbstractRedisAction {

    @Override
    public void execute(Context context) {
        BStruct bConnector = (BStruct) context.getRefArgument(0);
        RedisDataSource redisDataSource = (RedisDataSource) bConnector.getNativeData(Constants.REDIS_CLIENT);

        String key = context.getStringArgument(0);
        BMap<String, BFloat> bMap = (BMap<String, BFloat>) context.getRefArgument(1);
        if (bMap == null) {
            throw new BallerinaException("Member Map " + MUST_NOT_BE_NULL);
        }
        Map<String, Double> valueScoreMap = new HashMap<>(bMap.size());
        bMap.keySet().forEach(value -> valueScoreMap.put(value, (bMap.get(value).floatValue())));

        BInteger result = zAdd(key, redisDataSource, valueScoreMap);
        try {
            context.setReturnValues(result);
        } catch (Throwable e) {
            context.setReturnValues(RedisDataSourceUtils.getRedisConnectorError(context, e));
        }
    }
}
