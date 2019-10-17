import os
from typing import List

import api

cache_file = '../cache_ids.temp'
per_page = 100
usd_rate = 27.5
apartments_for_rent_params = {
    "category": 1,
    "realty_type": 2,
    "operation_type": 3,
    "city_id": 10
}


def crawl():
    ids = all_apartments_ids()
    for apartment_id in ids:
        apartment = api.http_get(f'/info/{apartment_id}')
        features = parse_apartment(apartment_id, apartment)
        print(features)
        append2csv(features)


def all_apartments_ids() -> List[str]:
    if os.path.isfile(cache_file):
        return open(cache_file).read().splitlines()

    ids = load_apartment_ids()

    with open(cache_file, 'a') as temp:
        for apt in ids:
            temp.write('%s\n' % apt)

    return ids


def load_apartment_ids() -> List[str]:
    all_apartments = []
    count = api.http_get('/search', apartments_for_rent_params)['count'] / per_page
    for i in range(0, int(count) + 1):
        apartments_per_page = api.http_get('/search', {
            **apartments_for_rent_params,
            "page": i})['items']
        all_apartments += apartments_per_page

    return all_apartments


def parse_apartment(apt_id: str, apt: dict):
    price = apt.get('price', apt.get('price_total', 0))
    if apt.get('currency_type', 'грн') == '$':
        price *= usd_rate
    features = apt.get('characteristics_values', {})
    return [apt_id,
            apt.get('total_square_meters', ''),
            apt.get('rooms_count', ''),
            apt.get('floor', ''),
            apt.get('floors_count', ''),
            apt.get('wall_type', ''),
            apt.get('district_name', ''),
            apt.get('city_name', ''),
            apt.get('state_name', ''),
            apt.get('metro_station_name', ''),
            apt.get('metro_station_brunch', ''),
            # furnished
            1 if bool(features.get('1480', '')) else 0,
            # with heating
            1 if bool(features.get('1478', '')) else 0,
            # with repair
            1 if bool(features.get('1479', '')) else 0,
            # with balcony
            1 if bool(features.get('1489', '')) else 0,
            # jacuzzi
            1 if bool(features.get('1481', '')) else 0,
            1 if bool(apt.get('with_panoramas', '')) else 0,
            apt.get('publishing_date', ''),
            float(price)
            ]


def append2csv(columns: List[str]):
    with open('../apartments_kyiv.csv', 'a', encoding='utf8') as csv:
        columns = map(lambda f: str(f), columns)
        line = ','.join(columns)
        csv.write('%s\n' % line)


if __name__ == '__main__':
    crawl()
