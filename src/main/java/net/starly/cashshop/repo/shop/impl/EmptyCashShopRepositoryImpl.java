package net.starly.cashshop.repo.shop.impl;

import net.starly.cashshop.repo.shop.CashShopRepository;

import java.util.Collections;
import java.util.List;

public class EmptyCashShopRepositoryImpl implements CashShopRepository {
    @Override
    public List<String> getShopNames() {
        return Collections.emptyList();
    }
}
