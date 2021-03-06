$(document).ready(function () {

  $('#district').unbind('all')
    .html(populateDistricts())
    .on('change', function () {
      const id = $(this).children(':selected').attr('id');
      const metro = metros[id];
      let result = '';
      for (let s in metro) {
        const station = metro[s];
        let sValue = (station === '--- немає метро поруч ---') ? '' : station;
        result += `<option value='${sValue}'>${station}</option>`;
      }
      $('#metro').html(result);
    });

  $('#wallType').unbind('all').html(populateWallTypes());

  loadLastForm();
});

function loadLastForm() {
  const form = $('#lastForm');
  selectOption(form, 'area');
  selectOption(form, 'district');
  selectOption(form, 'metro');
  selectOption(form, 'wallType');
  form.remove();
}

function selectOption(form, key) {
  if (form.attr(key)) {
    $(`#${key}`).val(form.attr(key)).change()
  }
}

function populateDistricts() {
  let options = '';
  for (let d in districts) {
    const dist = districts[d];
    options += `<option id='dist_${d}' value='${dist}'>${dist}</option>`;
  }
  return options;
}

function populateWallTypes() {
  let options = '';
  for (let w in wallTypes) {
    const type = wallTypes[w];
    options += `<option value='${type}'>${type}</option>`;
  }

  return options;
}

$('.count').each(function () {
  $(this).prop('Counter', 0).animate({
    Counter: $(this).text()
  }, {
    duration: 2000,
    easing: 'swing',
    step: function (now) {
      $(this).text(Math.ceil(now));
    }
  });
});

const districts = ['Святошинский', 'Оболонский', 'Деснянский', 'Голосеевский', 'Печерский', 'Соломенский', 'Подольский', 'Днепровский', 'Дарницкий', 'Шевченковский'];

const metros = {
  'dist_0': ['--- немає метро поруч ---', 'Святошин', 'Житомирская', 'Академгородок', 'Васильковская', 'Выставочный центр', 'Берестейская', 'Сырец', 'Политехнический институт', 'Печерская', 'Нивки', 'Шулявская'],
  'dist_1': ['--- немає метро поруч ---', 'Героев Днепра', 'Петровка', 'Оболонь', 'Минская', 'Нивки'],
  'dist_2': ['--- немає метро поруч ---', 'Дарница', 'Петровка', 'Лесная', 'Черниговская', 'Оболонь', 'Левобережная'],
  'dist_3': ['--- немає метро поруч ---', 'Теремки', 'Университет', 'Васильковская', 'Выставочный центр', 'Дворец спорта', 'Голосеевская', 'Олимпийская', 'Вокзальная', 'Ипподром', 'Площадь Льва Толстого', 'Дворец Украина', 'Демиевская', 'Лыбедская'],
  'dist_4': ['--- немає метро поруч ---', 'Дворец спорта', 'Олимпийская', 'Золотые ворота', 'Выдубичи', 'Дорогожичи', 'Крещатик', 'Площадь Независимости', 'Арсенальная', 'Лыбедская', 'Кловская', 'Дворец Украина', 'Площадь Льва Толстого', 'Театральная', 'Дружбы народов', 'Печерская', 'Лукьяновская'],
  'dist_5': ['--- немає метро поруч ---', 'Васильковская', 'Дворец спорта', 'Олимпийская', 'Вокзальная', 'Берестейская', 'Дорогожичи', 'Политехнический институт', 'Осокорки', 'Демиевская', 'Шулявская'],
  'dist_6': ['--- немає метро поруч ---', 'Кловская', 'Петровка', 'Почтовая площадь', 'Дорогожичи', 'Сырец', 'Тараса Шевченко', 'Оболонь', 'Минская', 'Нивки', 'Лукьяновская', 'Контрактовая площадь'],
  'dist_7': ['--- немає метро поруч ---', 'Позняки', 'Шулявская', 'Дарница', 'Университет', 'Харьковская', 'Петровка', 'Лесная', 'Черниговская', 'Дружбы народов', 'Осокорки', 'Нивки', 'Лукьяновская', 'Левобережная'],
  'dist_8': ['--- немає метро поруч ---', 'Левобережная', 'Дарница', 'Славутич', 'Вырлица', 'Харьковская', 'Черниговская', 'Красный хутор', 'Осокорки', 'Позняки', 'Бориспольская'],
  'dist_9': ['--- немає метро поруч ---', 'Университет', 'Академгородок', 'Дворец спорта', 'Олимпийская', 'Вокзальная', 'Золотые ворота', 'Берестейская', 'Дорогожичи', 'Сырец', 'Политехнический институт', 'Крещатик', 'Площадь Независимости', 'Шулявская', 'Контрактовая площадь', 'Площадь Льва Толстого', 'Театральная', 'Тараса Шевченко', 'Нивки', 'Лукьяновская'],
};

const wallTypes = ['блочно-кирпичный', 'пеноблок', 'газоблок', 'монолитный железобетон', 'керамзитобетон', 'СИП', 'силикатный кирпич', 'ракушечник(ракушняк)', 'кирпич', 'армированный железобетон', 'монолитно-блочный', 'монолит', 'монолитно-каркасный', 'монолитно-кирпичный', 'сборно-монолитная', 'газобетон', 'панель', 'железобетон'];
