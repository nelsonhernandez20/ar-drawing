-- Supabase setup for AR Draw template catalog
-- Run in Supabase SQL Editor, then upload images to Storage bucket "templates"

create table if not exists public.images (
    id text primary key,
    title text not null default '',
    thumbnail_url text not null,
    image_url text not null,
    is_premium boolean not null default false,
    category text not null default 'animals',
    sort_order int not null default 0,
    created_at timestamptz not null default now()
);

-- Migration for existing projects (safe to re-run):
alter table public.images
    add column if not exists category text not null default 'animals';
update public.images set category = 'animals' where category is null or category = '';

alter table public.images enable row level security;

create policy "Public read images"
    on public.images for select
    using (true);

-- Categories: 3d | animemes | animals | cars
-- Storage: create bucket "templates" (public) with folders free/ and premium/
-- Example row after uploading files:
-- insert into public.images (id, title, thumbnail_url, image_url, is_premium, category, sort_order) values
-- ('free-01', 'Rostro anime', 'https://YOUR_PROJECT.supabase.co/storage/v1/object/public/templates/free/free-01-thumb.jpg', 'https://YOUR_PROJECT.supabase.co/storage/v1/object/public/templates/free/free-01.jpg', false, 'animals', 1);
