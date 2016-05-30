package com.bitcup.shoppee.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.bitcup.shoppee.api.dao.ShoppingListDao;
import com.bitcup.shoppee.api.dto.ShoppingListDto;

import java.util.List;

/**
 * @author bitcup
 */
public class GetListsRequestHandler implements RequestHandler<String, List<ShoppingListDto>> {

    private final ShoppingListDao dao = new ShoppingListDao();

    @Override
    public List<ShoppingListDto> handleRequest(String userId, Context context) {
        return dao.getShoppingLists(userId);
    }

}
