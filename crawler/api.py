import json
import requests

base_site = 'https://developers.ria.com/dom'
api_key = 'KepwQJaddqwLL4hzvOQXF4DQzicr2WOXDHee5TaH'


def http_get(request: str, params={}) -> str:
    params = {**params, **token()}
    r = requests.get(f'{base_site}{request}', params)
    print(f'[{r.status_code} {r.reason}] Request {r.url}')

    if r.status_code == 200:
        return json.loads(r.text)

    return ''


def token() -> dict:
    return {
        'lang_id': 4,
        'api_key': api_key
    }
