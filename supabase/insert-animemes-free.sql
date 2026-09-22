-- AniMemes gratis (10 imágenes de ~/Downloads/animemes-normal)
--
-- 1) En Storage → bucket público `templates` → carpeta `free/`
--    sube los archivos con ESTOS nombres (renómbralos al subir):
--
--    Picsart_26-08-29_14-44-13-767.jpg        → animemes-free-01.jpg
--    Picsart_26-08-29_14-46-04-427.jpg        → animemes-free-02.jpg
--    Picsart_26-08-29_17-50-49-069.jpg        → animemes-free-03.jpg
--    Picsart_26-08-29_17-51-00-071.jpg        → animemes-free-04.jpg
--    Picsart_26-08-29_17-51-12-140.jpg        → animemes-free-05.jpg
--    Picsart_26-09-03_13-52-30-445(1).jpg     → animemes-free-06.jpg
--    Picsart_26-09-03_13-52-42-424(1).jpg     → animemes-free-07.jpg
--    Picsart_26-09-03_13-53-18-393(1).jpg     → animemes-free-08.jpg
--    Picsart_26-09-03_16-02-12-806(1).jpg     → animemes-free-09.jpg
--    Picsart_26-09-03_16-02-38-943(1).jpg     → animemes-free-10.jpg
--
-- 2) Asegúrate de tener la columna category (si no la corriste aún):
alter table public.images
    add column if not exists category text not null default 'animals';

-- 3) Inserta las filas (thumb e imagen usan la misma URL, como el catálogo actual):
insert into public.images (id, title, thumbnail_url, image_url, is_premium, category, sort_order) values
(
  'animemes-free-01',
  'AniMeme 1',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-01.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-01.jpg',
  false, 'animemes', 101
),
(
  'animemes-free-02',
  'AniMeme 2',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-02.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-02.jpg',
  false, 'animemes', 102
),
(
  'animemes-free-03',
  'AniMeme 3',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-03.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-03.jpg',
  false, 'animemes', 103
),
(
  'animemes-free-04',
  'AniMeme 4',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-04.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-04.jpg',
  false, 'animemes', 104
),
(
  'animemes-free-05',
  'AniMeme 5',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-05.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-05.jpg',
  false, 'animemes', 105
),
(
  'animemes-free-06',
  'AniMeme 6',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-06.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-06.jpg',
  false, 'animemes', 106
),
(
  'animemes-free-07',
  'AniMeme 7',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-07.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-07.jpg',
  false, 'animemes', 107
),
(
  'animemes-free-08',
  'AniMeme 8',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-08.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-08.jpg',
  false, 'animemes', 108
),
(
  'animemes-free-09',
  'AniMeme 9',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-09.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-09.jpg',
  false, 'animemes', 109
),
(
  'animemes-free-10',
  'AniMeme 10',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-10.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/free/animemes-free-10.jpg',
  false, 'animemes', 110
)
on conflict (id) do update set
  title = excluded.title,
  thumbnail_url = excluded.thumbnail_url,
  image_url = excluded.image_url,
  is_premium = excluded.is_premium,
  category = excluded.category,
  sort_order = excluded.sort_order;
