import json
import requests

from datetime import datetime
from time import sleep

base_site = 'https://developers.ria.com/dom'
api_key = 'KepwQJaddqwLL4hzvOQXF4DQzicr2WOXDHee5TaH'


def http_get(request: str, params={}, wait_onfail_min=60) -> dict:
    params = {**params, **token()}
    r = requests.get(f'{base_site}{request}', params)
    log(f'[{r.status_code} {r.reason}] Request {r.url}')

    if r.status_code == 429:
        log(f'Error {r.status_code}: waiting {wait_onfail_min} minutes.')
        sleep(wait_onfail_min * 60)
        return http_get(request, params)

    return json.loads(r.text)


def token() -> dict:
    return {
        'lang_id': 4,
        'api_key': api_key
    }


def log(msg: str):
    timestamp = str(datetime.now())
    print(f'[{timestamp}] {msg}')
