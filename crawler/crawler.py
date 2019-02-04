import os
from typing import List

import api

cache_file = '../cache_ids.temp'
per_page = 100
usd_rate = 27.5
apartments_for_rent_params = {
    "category": 1,
    "realty_type": 2,
    "operation_type": 3
}


def crawl():
    ids = all_apartments_ids()
    for apartment_id in ids:
        apartment: dict = api.http_get(f'/info/{apartment_id}')
        price = apartment.get('price', 0)
        if apartment.get('currency_type', 'грн') == '$':
            price *= usd_rate
        characteristics_values = apartment.get('characteristics_values', {})
        features = [apartment_id,
                    apartment.get('total_square_meters', ''),
                    apartment.get('rooms_count', ''),
                    apartment.get('floor', ''),
                    apartment.get('floors_count', ''),
                    apartment.get('wall_type_uk', apartment.get('wall_type', '')),
                    apartment.get('street_name_uk', apartment.get('street_name', '')),
                    apartment.get('district_name_uk', apartment.get('district_name', '')),
                    apartment.get('city_name_uk', apartment.get('city_name', '')),
                    apartment.get('state_name_uk', apartment.get('state_name', '')),
                    apartment.get('metro_station_name_uk', apartment.get('metro_station_name', '')),
                    apartment.get('metro_station_brunch', ''),
                    bool(characteristics_values.get('1480', '')),  # furnished
                    bool(characteristics_values.get('1478', '')),  # with heating
                    bool(characteristics_values.get('1479', '')),  # with repair
                    bool(characteristics_values.get('1489', '')),  # with balcony
                    bool(characteristics_values.get('1481', '')),  # jacuzzi
                    bool(apartment.get('with_panoramas', '')),
                    apartment.get('publishing_date', ''),
                    int(price)
                    ]
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


def append2csv(columns: List[str]):
    with open('../apartments.csv', 'a', encoding='utf8') as csv:
        columns = map(lambda f: str(f), columns)
        line = ', '.join(columns)
        csv.write('%s\n' % line)


if __name__ == '__main__':
    crawl()
